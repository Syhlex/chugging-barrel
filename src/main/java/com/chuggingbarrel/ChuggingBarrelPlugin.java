package com.chuggingbarrel;

import com.google.common.base.Strings;
import com.google.inject.Provides;

import javax.inject.Inject;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.FontID;
import net.runelite.api.ScriptID;
import net.runelite.api.events.VarbitChanged;
import net.runelite.api.widgets.*;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.chatbox.ChatboxPanelManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

@Slf4j
@PluginDescriptor(
    name = "Chugging Barrel"
)
public class ChuggingBarrelPlugin extends Plugin {
    @Inject
    private Client client;

    @Inject
    private ChuggingBarrelConfig config;

    @Inject
    private ClientThread clientThread;

    @Inject
    private ConfigManager configManager;

    @Inject
    private ChatboxPanelManager chatboxPanelManager;

    private final int LOADOUTS_SCROLLBAR_ID = 983092;
    private final int LOADOUTS_SCROLL_CONTAINER_ID = 983093;

    private final int[] LOADOUT_CONTAINER_IDS = {983094, 983095, 983096, 983097};
    private final int[] POTION_CONTAINER_IDS = {983109, 983100, 983103, 983106};
    private final int[] LOAD_BUTTON_IDS = {983107, 983098, 983101, 983104};

    private final int CHUGGING_BARREL_VARBIT_ID = 9727; // Same as the rune pouch
    private final int CHUGGING_BARREL_VARBIT_VALUE_ON = 5;
    private final int CHUGGING_BARREL_VARBIT_VALUE_OFF = 0;

    private final int TITLE_WIDGET_HEIGHT = 20;

    private final int[] loadoutContainerOriginalY = new int[LOADOUT_CONTAINER_IDS.length];
    private final int[] loadoutContainerOriginalHeight = new int[LOADOUT_CONTAINER_IDS.length];

    @Provides
    ChuggingBarrelConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(ChuggingBarrelConfig.class);
    }

    @Override
    protected void startUp() throws Exception {
        clientThread.invokeLater(this::setupChuggingBarrelInterface);
    }

    @Override
    protected void shutDown() throws Exception {
        clientThread.invokeLater(this::resetChuggingBarrelInterface);
    }

    @Subscribe
    public void onVarbitChanged(VarbitChanged varbitChanged) {
        if (varbitChanged.getVarbitId() == CHUGGING_BARREL_VARBIT_ID) {
            if (varbitChanged.getValue() == CHUGGING_BARREL_VARBIT_VALUE_ON) {
                clientThread.invokeLater(this::setupChuggingBarrelInterface);
            } else if (varbitChanged.getValue() == CHUGGING_BARREL_VARBIT_VALUE_OFF) {
                clientThread.invokeLater(this::resetChuggingBarrelInterface);
            }
        }

    }

    private void setupChuggingBarrelInterface() {
        log.info("Setting up");
        updateLoadout(LOADOUT_CONTAINER_IDS[0], POTION_CONTAINER_IDS[0], LOAD_BUTTON_IDS[0], 0, 0);
        updateLoadout(LOADOUT_CONTAINER_IDS[1], POTION_CONTAINER_IDS[1], LOAD_BUTTON_IDS[1], 20, 1);
        updateLoadout(LOADOUT_CONTAINER_IDS[2], POTION_CONTAINER_IDS[2], LOAD_BUTTON_IDS[2], 40, 2);
        updateLoadout(LOADOUT_CONTAINER_IDS[3], POTION_CONTAINER_IDS[3], LOAD_BUTTON_IDS[3], 60, 3);
        updateScrollbar(true);
    }

    private void resetChuggingBarrelInterface() {
        log.info("Resetting");
        resetLoadout(LOADOUT_CONTAINER_IDS[0], POTION_CONTAINER_IDS[0], LOAD_BUTTON_IDS[0], 0);
        resetLoadout(LOADOUT_CONTAINER_IDS[1], POTION_CONTAINER_IDS[1], LOAD_BUTTON_IDS[1], 1);
        resetLoadout(LOADOUT_CONTAINER_IDS[2], POTION_CONTAINER_IDS[2], LOAD_BUTTON_IDS[2], 2);
        resetLoadout(LOADOUT_CONTAINER_IDS[3], POTION_CONTAINER_IDS[3], LOAD_BUTTON_IDS[3], 3);
        updateScrollbar(false);
    }

    private void resetLoadout(int loadoutContainerId, int potionsContainerId, int loadButtonId, int index) {
        Widget loadoutContainer = client.getWidget(loadoutContainerId);
        if (loadoutContainer == null) {
            return;
        }

        loadoutContainer.setOriginalY(loadoutContainerOriginalY[index]);
        loadoutContainer.setOriginalHeight(loadoutContainerOriginalHeight[index]);
        loadoutContainer.revalidate();

        Widget textWidget = loadoutContainer.getChild(1);
        if (textWidget != null) {
            textWidget.setHidden(true);
            textWidget.revalidate();
        }

        Widget potionsContainer = client.getWidget(potionsContainerId);
        if (potionsContainer != null) {
            potionsContainer.setYPositionMode(WidgetPositionMode.ABSOLUTE_TOP);
            potionsContainer.setHeightMode(WidgetSizeMode.MINUS);
            potionsContainer.setOriginalHeight(0);
            potionsContainer.revalidate();
        }

        Widget loadButton = client.getWidget(loadButtonId);
        if (loadButton != null) {
            loadButton.setOriginalY(4);
            loadButton.revalidate();
        }
    }

    private void updateLoadout(int loadoutContainerId, int potionsContainerId, int loadButtonId, int yOffset, int index) {
        Widget loadoutContainer = client.getWidget(loadoutContainerId);
        if (loadoutContainer == null) {
            return;
        }

        loadoutContainerOriginalY[index] = loadoutContainer.getOriginalY();
        loadoutContainerOriginalHeight[index] = loadoutContainer.getHeight();

        loadoutContainer.setOriginalY(loadoutContainer.getOriginalY() + yOffset);
        loadoutContainer.setOriginalHeight(loadoutContainer.getHeight() + TITLE_WIDGET_HEIGHT);

        Widget potionsContainer = client.getWidget(potionsContainerId);
        if (potionsContainer != null) {
            potionsContainer.setYPositionMode(WidgetPositionMode.ABSOLUTE_BOTTOM);
            potionsContainer.setHeightMode(WidgetSizeMode.ABSOLUTE);
            potionsContainer.setOriginalHeight(74);
        }

        Widget loadButton = client.getWidget(loadButtonId);
        if (loadButton != null) {
            loadButton.setOriginalY(loadButton.getRelativeY() + TITLE_WIDGET_HEIGHT);
            loadButton.revalidate();
        }

        createLoadoutTitle(loadoutContainer, index);
    }

    private void createLoadoutTitle(Widget loadoutContainer, int index) {
        String defaultLoadoutName = "Loadout " + (index + 1);
        String savedLoadoutName = configManager.getConfiguration(ChuggingBarrelConfig.GROUP, String.valueOf(index));

        Widget textWidget = loadoutContainer.createChild(WidgetType.TEXT);

        textWidget.setText(savedLoadoutName != null ? savedLoadoutName : defaultLoadoutName);
        textWidget.setFontId(FontID.BOLD_12);
        textWidget.setXTextAlignment(WidgetTextAlignment.CENTER);
        textWidget.setTextShadowed(true);
        textWidget.setTextColor(16750623);
        textWidget.setOriginalHeight(TITLE_WIDGET_HEIGHT);
        textWidget.setOriginalWidth(loadoutContainer.getWidth());
        textWidget.setYTextAlignment(WidgetTextAlignment.CENTER);
        textWidget.setHasListener(true);
        textWidget.setAction(0, "Edit");
        textWidget.setName("loadout name");
        textWidget.setOnOpListener((JavaScriptCallback) event -> {
            String oldLoadoutName = textWidget.getText();
            chatboxPanelManager.openTextInput("Loadout name: ")
                .value(Strings.nullToEmpty(oldLoadoutName))
                .onDone((value) -> {
                    String newLoadoutName = value.trim();
                    if (newLoadoutName.isEmpty()) {
                        newLoadoutName = defaultLoadoutName;
                    }
                    configManager.setConfiguration(ChuggingBarrelConfig.GROUP, String.valueOf(index), newLoadoutName);
                    textWidget.setText(newLoadoutName);
                })
                .build();
        });
        textWidget.revalidate();
    }

    private void updateScrollbar(boolean isSetup) {
        Widget scrollContainer = client.getWidget(LOADOUTS_SCROLL_CONTAINER_ID);
        Widget loadoutContainer = client.getWidget(LOADOUT_CONTAINER_IDS[0]);
        if (scrollContainer == null || loadoutContainer == null) {
            return;
        }

        int y = 2;
        y += (isSetup ? TITLE_WIDGET_HEIGHT * 4 : 0) + loadoutContainer.getHeight() * 4;
        y += 8;

        int newHeight = 0;

        if (scrollContainer.getScrollHeight() > 0) {
            newHeight = (scrollContainer.getScrollY() * y) / scrollContainer.getScrollHeight();
        }

        scrollContainer.setScrollHeight(y);
        scrollContainer.revalidateScroll();

        client.runScript(ScriptID.UPDATE_SCROLLBAR, LOADOUTS_SCROLLBAR_ID, LOADOUTS_SCROLL_CONTAINER_ID, newHeight);
    }
}
