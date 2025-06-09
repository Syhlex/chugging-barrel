package com.chuggingbarrel.features.notbankedwarning;

import com.chuggingbarrel.ChuggingBarrelConfig;
import com.chuggingbarrel.ChuggingBarrelConstants;
import com.chuggingbarrel.module.PluginLifecycleComponent;
import net.runelite.api.*;
import net.runelite.api.events.*;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;
import java.util.Objects;

public class NotBankedWarning implements PluginLifecycleComponent {
    @Inject
    private EventBus eventBus;

    @Inject
    private Client client;

    @Inject
    private OverlayManager overlayManager;

    @Inject
    private NotBankedOverlay notBankedOverlay;

    @Inject
    private ChuggingBarrelConfig config;

    private boolean isBarrelClicked;

    private Long startTimeMillis;

    @Override
    public void startup() {
        eventBus.register(this);
    }

    @Override
    public void shutdown() {
        eventBus.unregister(this);
        overlayManager.remove(notBankedOverlay);
    }

    @Subscribe
    public void onScriptPreFired(ScriptPreFired event) {
        if (event.getScriptId() == ChuggingBarrelConstants.ITEM_CLICK_SCRIPT) {
            Object[] args = event.getScriptEvent().getArguments();
            if (Objects.equals(args[2].toString(), String.valueOf(ChuggingBarrelConstants.ITEM_ID))) {
                isBarrelClicked = true;
            }
        }
    }

    @Subscribe
    public void onChatMessage(ChatMessage event) {
        if (!isBarrelClicked || !config.showNotBankedWarning()) {
            return;
        }

        if (event.getType() == ChatMessageType.SPAM && event.getMessage().startsWith("You drink")) {
            overlayManager.add(notBankedOverlay);
            startTimeMillis = System.currentTimeMillis();
            isBarrelClicked = false;
        } else if (event.getType() == ChatMessageType.GAMEMESSAGE) {
            String message = event.getMessage();
            if (Objects.equals(message, ChuggingBarrelConstants.BANK_PROXIMITY_ERROR) || Objects.equals(message, ChuggingBarrelConstants.BARREL_EMPTY_ERROR)) {
                isBarrelClicked = false;
            }
        }
    }

    @Subscribe
    public void onItemContainerChanged(ItemContainerChanged event) {
        if (event.getItemContainer() != client.getItemContainer(InventoryID.INVENTORY)) {
            return;
        }

        if (!event.getItemContainer().contains(ItemID.CHUGGING_BARREL)) {
            removeWarning();
        }
    }

    @Subscribe
    public void onActorDeath(ActorDeath event) {
        if (event.getActor() == client.getLocalPlayer()) {
            removeWarning();
        }
    }

    @Subscribe
    public void onGameTick(GameTick event) {
        if (startTimeMillis != null) {
            long elapsedMillis = System.currentTimeMillis() - startTimeMillis;
            if (elapsedMillis > config.notBankedWarningTimeout() * 1000L) {
                removeWarning();
            }
        } else {
            removeWarning();
        }
    }

    private void removeWarning() {
        startTimeMillis = null;
        overlayManager.remove(notBankedOverlay);
    }
}
