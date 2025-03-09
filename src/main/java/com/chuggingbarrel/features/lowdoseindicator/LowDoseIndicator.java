package com.chuggingbarrel.features.lowdoseindicator;

import com.chuggingbarrel.ChuggingBarrelConfig;
import com.chuggingbarrel.ChuggingBarrelConstants;
import com.chuggingbarrel.state.ConfigStateManager;
import com.chuggingbarrel.state.PotionInfo;
import com.chuggingbarrel.module.PluginLifecycleComponent;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.ScriptPostFired;
import net.runelite.api.events.ScriptPreFired;
import net.runelite.api.widgets.Widget;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;
import java.util.Objects;

@Slf4j
public class LowDoseIndicator implements PluginLifecycleComponent {
    @Inject
    private EventBus eventBus;

    @Inject
    private Client client;

    @Inject
    private ChuggingBarrelConfig config;

    @Inject
    private ConfigManager configManager;

    @Inject
    private OverlayManager overlayManager;

    @Inject
    private LowDoseOverlay lowDoseOverlay;

    private boolean isBarrelClicked = false;

    @Inject
    private ConfigStateManager configStateManager;

    @Override
    public void startup() {
        eventBus.register(this);
        overlayManager.add(lowDoseOverlay);
    }

    @Override
    public void shutdown() {
        eventBus.unregister(this);
        overlayManager.remove(lowDoseOverlay);
    }

    @Subscribe
    public void onScriptPostFired(ScriptPostFired event) {
        int scriptId = event.getScriptId();
        if (scriptId == ChuggingBarrelConstants.BARREL_BANK_INTERFACE_DOSE_CHANGE_SCRIPT) {
            updatePotionDoses(ChuggingBarrelConstants.BANK_CURRENT_POTIONS_ID);
        } else if (scriptId == ChuggingBarrelConstants.BARREL_INVENTORY_INTERFACE_DOSE_CHANGE_SCRIPT) {
            updatePotionDoses(ChuggingBarrelConstants.INVENTORY_CURRENT_POTIONS_ID);
        }
    }

    private void updatePotionDoses(int potionsWidgetId) {
        Widget currentPotionsWidget = client.getWidget(potionsWidgetId);
        if (currentPotionsWidget == null) {
            return;
        }

        for (int i = 0; i < 5; i++) {
            Widget currentPotion = currentPotionsWidget.getChild(i);
            if (currentPotion != null) {
                String potionName = currentPotion.getName();
                int doseCount = currentPotion.getItemQuantity();
                configStateManager.updatePotionInfo(i, Objects.equals(potionName, "null") ? null : new PotionInfo(potionName, doseCount));
            }
        }
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
        if (!isBarrelClicked) {
            return;
        }

        if (event.getType() == ChatMessageType.SPAM && event.getMessage().startsWith("You drink")) {
            decrementDoses();
            isBarrelClicked = false;
        } else if (event.getType() == ChatMessageType.GAMEMESSAGE) {
            String message = event.getMessage();
            if (Objects.equals(message, ChuggingBarrelConstants.BANK_PROXIMITY_ERROR) || Objects.equals(message, ChuggingBarrelConstants.BARREL_EMPTY_ERROR)) {
                isBarrelClicked = false;
            }
        }
    }

    public boolean isLowOnDoses() {
        for (int i = 0; i < 5; i++) {
            PotionInfo potionInfo = configStateManager.getPotionInfo(i);
            if (potionInfo != null && potionInfo.getDoses() <= config.lowDoseThreshold()) {
                return true;
            }
        }
        return false;
    }

    private void decrementDoses() {
        for (int i = 0; i < 5; i++) {
            PotionInfo potionInfo = configStateManager.getPotionInfo(i);
            if (potionInfo != null && potionInfo.getDoses() > 0) {
                configStateManager.updatePotionInfo(i, new PotionInfo(potionInfo.getName(), potionInfo.getDoses() - 1));
            }
        }
    }
}
