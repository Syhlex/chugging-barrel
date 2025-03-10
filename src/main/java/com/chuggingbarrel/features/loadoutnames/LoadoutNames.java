package com.chuggingbarrel.features.loadoutnames;

import com.chuggingbarrel.ChuggingBarrelConfig;
import com.chuggingbarrel.ChuggingBarrelConstants;
import com.chuggingbarrel.module.PluginLifecycleComponent;
import com.google.common.base.Strings;
import net.runelite.api.Client;
import net.runelite.api.FontID;
import net.runelite.api.ScriptID;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.api.events.VarbitChanged;
import net.runelite.api.widgets.*;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.chatbox.ChatboxPanelManager;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;

public class LoadoutNames implements PluginLifecycleComponent {
    @Inject
    private EventBus eventBus;

    @Inject
    private Client client;

    @Inject
    private ClientThread clientThread;

    @Inject
    private ConfigManager configManager;

    @Inject
    private ChatboxPanelManager chatboxPanelManager;

    @Inject
    private OverlayManager overlayManager;

    @Inject
    private LoadoutOverlay loadoutOverlay;

    private final int[] loadoutContainerOriginalY = new int[ChuggingBarrelConstants.LOADOUT_CONTAINER_IDS.length];
    private final int[] loadoutContainerOriginalHeight = new int[ChuggingBarrelConstants.LOADOUT_CONTAINER_IDS.length];

    @Override
    public void startup() {
        eventBus.register(this);
        overlayManager.add(loadoutOverlay);
        clientThread.invokeLater(this::setupChuggingBarrelInterface);
    }

    @Override
    public void shutdown() {
        eventBus.unregister(this);
        overlayManager.remove(loadoutOverlay);
        clientThread.invokeLater(this::resetChuggingBarrelInterface);
    }

    @Subscribe
    public void onVarbitChanged(VarbitChanged varbitChanged) {
        if (varbitChanged.getVarbitId() != ChuggingBarrelConstants.CHUGGING_BARREL_VARBIT_ID) {
            return;
        }

        if (varbitChanged.getValue() == ChuggingBarrelConstants.CHUGGING_BARREL_VARBIT_VALUE_ON) {
            clientThread.invokeLater(this::setupChuggingBarrelInterface);
        } else if (varbitChanged.getValue() == ChuggingBarrelConstants.CHUGGING_BARREL_VARBIT_VALUE_OFF) {
            clientThread.invokeLater(this::resetChuggingBarrelInterface);
        }
    }

    @Subscribe
    public void onMenuOptionClicked(MenuOptionClicked event) {
        Widget widget = event.getWidget();

        if (widget == null) {
            return;
        }

        for (int i = 0; i < ChuggingBarrelConstants.LOAD_BUTTON_IDS.length; i++) {
            if (widget.getId() == ChuggingBarrelConstants.LOAD_BUTTON_IDS[i]) {
                configManager.setConfiguration(ChuggingBarrelConfig.GROUP, ChuggingBarrelConfig.SELECTED_LOADOUT_INDEX, i);
                return;
            }
        }

        if (widget.getId() == ChuggingBarrelConstants.DEPOSIT_POTIONS_ID) {
            configManager.unsetConfiguration(ChuggingBarrelConfig.GROUP, ChuggingBarrelConfig.SELECTED_LOADOUT_INDEX);
        }
    }

    public String getSelectedLoadoutName() {
        String selectedLoadoutIndex = configManager.getConfiguration(ChuggingBarrelConfig.GROUP, ChuggingBarrelConfig.SELECTED_LOADOUT_INDEX);
        return configManager.getConfiguration(ChuggingBarrelConfig.GROUP, selectedLoadoutIndex);
    }

    private void setupChuggingBarrelInterface() {
        updateLoadout(ChuggingBarrelConstants.LOADOUT_CONTAINER_IDS[0], ChuggingBarrelConstants.POTION_CONTAINER_IDS[0], ChuggingBarrelConstants.LOAD_BUTTON_IDS[0], 0, 0);
        updateLoadout(ChuggingBarrelConstants.LOADOUT_CONTAINER_IDS[1], ChuggingBarrelConstants.POTION_CONTAINER_IDS[1], ChuggingBarrelConstants.LOAD_BUTTON_IDS[1], 20, 1);
        updateLoadout(ChuggingBarrelConstants.LOADOUT_CONTAINER_IDS[2], ChuggingBarrelConstants.POTION_CONTAINER_IDS[2], ChuggingBarrelConstants.LOAD_BUTTON_IDS[2], 40, 2);
        updateLoadout(ChuggingBarrelConstants.LOADOUT_CONTAINER_IDS[3], ChuggingBarrelConstants.POTION_CONTAINER_IDS[3], ChuggingBarrelConstants.LOAD_BUTTON_IDS[3], 60, 3);
        updateScrollbar(true);
    }

    private void resetChuggingBarrelInterface() {
        resetLoadout(ChuggingBarrelConstants.LOADOUT_CONTAINER_IDS[0], ChuggingBarrelConstants.POTION_CONTAINER_IDS[0], ChuggingBarrelConstants.LOAD_BUTTON_IDS[0], 0);
        resetLoadout(ChuggingBarrelConstants.LOADOUT_CONTAINER_IDS[1], ChuggingBarrelConstants.POTION_CONTAINER_IDS[1], ChuggingBarrelConstants.LOAD_BUTTON_IDS[1], 1);
        resetLoadout(ChuggingBarrelConstants.LOADOUT_CONTAINER_IDS[2], ChuggingBarrelConstants.POTION_CONTAINER_IDS[2], ChuggingBarrelConstants.LOAD_BUTTON_IDS[2], 2);
        resetLoadout(ChuggingBarrelConstants.LOADOUT_CONTAINER_IDS[3], ChuggingBarrelConstants.POTION_CONTAINER_IDS[3], ChuggingBarrelConstants.LOAD_BUTTON_IDS[3], 3);
        updateScrollbar(false);
    }

    private void resetLoadout(int loadoutContainerId, int potionsContainerId, int loadButtonId, int index) {
        Widget loadoutContainer = client.getWidget(loadoutContainerId);
        if (loadoutContainer == null || loadoutContainerOriginalHeight[0] == 0) {
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
        if (loadoutContainer == null || loadoutContainer.isHidden()) {
            return;
        }

        loadoutContainerOriginalY[index] = loadoutContainer.getOriginalY();
        loadoutContainerOriginalHeight[index] = loadoutContainer.getHeight();

        loadoutContainer.setOriginalY(loadoutContainer.getOriginalY() + yOffset);
        loadoutContainer.setOriginalHeight(loadoutContainer.getHeight() + ChuggingBarrelConstants.TITLE_WIDGET_HEIGHT);

        Widget potionsContainer = client.getWidget(potionsContainerId);
        if (potionsContainer != null) {
            potionsContainer.setYPositionMode(WidgetPositionMode.ABSOLUTE_BOTTOM);
            potionsContainer.setHeightMode(WidgetSizeMode.ABSOLUTE);
            potionsContainer.setOriginalHeight(74);
        }

        Widget loadButton = client.getWidget(loadButtonId);
        if (loadButton != null) {
            loadButton.setOriginalY(loadButton.getRelativeY() + ChuggingBarrelConstants.TITLE_WIDGET_HEIGHT);
            loadButton.revalidate();
        }

        createLoadoutTitle(loadoutContainer, index);
    }

    private void createLoadoutTitle(Widget loadoutContainer, int index) {
        String defaultLoadoutName = "Loadout " + (index + 1);
        String savedLoadoutName = configManager.getConfiguration(ChuggingBarrelConfig.GROUP, String.valueOf(index));

        Widget textWidget = loadoutContainer.createChild(1, WidgetType.TEXT);

        textWidget.setText(savedLoadoutName != null ? savedLoadoutName : defaultLoadoutName);
        textWidget.setFontId(FontID.BOLD_12);
        textWidget.setXTextAlignment(WidgetTextAlignment.CENTER);
        textWidget.setTextShadowed(true);
        textWidget.setTextColor(16750623);
        textWidget.setOriginalHeight(ChuggingBarrelConstants.TITLE_WIDGET_HEIGHT);
        textWidget.setOriginalWidth(loadoutContainer.getWidth());
        textWidget.setYTextAlignment(WidgetTextAlignment.CENTER);
        textWidget.setHasListener(true);
        textWidget.setAction(0, "Edit");
        textWidget.setName("loadout name");
        textWidget.setOnOpListener((JavaScriptCallback) event -> {
            String oldLoadoutName = textWidget.getText();
            chatboxPanelManager.openTextInput(
                    "Loadout name:<br>" +
                        "(Only the first " + ChuggingBarrelConstants.OVERLAY_CHAR_LIMIT + " characters will be shown in the overlay)"
                )
                .value(Strings.nullToEmpty(oldLoadoutName))
                .onDone((value) -> {
                    clientThread.invokeLater(() -> {
                        String newLoadoutName = value.trim();
                        if (newLoadoutName.isEmpty()) {
                            newLoadoutName = defaultLoadoutName;
                        }
                        configManager.setConfiguration(ChuggingBarrelConfig.GROUP, String.valueOf(index), newLoadoutName);
                        textWidget.setText(newLoadoutName);
                        textWidget.revalidate();
                    });
                })
                .build();
        });
        textWidget.revalidate();
    }

    private void updateScrollbar(boolean isSetup) {
        Widget scrollContainer = client.getWidget(ChuggingBarrelConstants.LOADOUTS_SCROLL_CONTAINER_ID);
        Widget loadoutContainer = client.getWidget(ChuggingBarrelConstants.LOADOUT_CONTAINER_IDS[0]);
        if (scrollContainer == null || loadoutContainer == null || loadoutContainer.isHidden()) {
            return;
        }

        int y = 2;
        y += (isSetup ? ChuggingBarrelConstants.TITLE_WIDGET_HEIGHT * 4 : 0) + loadoutContainer.getHeight() * 4;
        y += 8;

        int newHeight = 0;

        if (scrollContainer.getScrollHeight() > 0) {
            newHeight = (scrollContainer.getScrollY() * y) / scrollContainer.getScrollHeight();
        }

        scrollContainer.setScrollHeight(y);
        scrollContainer.revalidateScroll();

        client.runScript(ScriptID.UPDATE_SCROLLBAR, ChuggingBarrelConstants.LOADOUTS_SCROLLBAR_ID, ChuggingBarrelConstants.LOADOUTS_SCROLL_CONTAINER_ID, newHeight);
    }
}
