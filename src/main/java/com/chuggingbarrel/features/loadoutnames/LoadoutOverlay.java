package com.chuggingbarrel.features.loadoutnames;

import com.chuggingbarrel.ChuggingBarrelConfig;
import com.chuggingbarrel.ChuggingBarrelConstants;
import net.runelite.api.widgets.WidgetItem;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.overlay.WidgetItemOverlay;
import net.runelite.client.ui.overlay.components.TextComponent;

import javax.inject.Inject;
import java.awt.*;

public class LoadoutOverlay extends WidgetItemOverlay {
    private final LoadoutNames feature;
    private final ChuggingBarrelConfig config;

    @Inject
    LoadoutOverlay(LoadoutNames feature, ChuggingBarrelConfig config) {
        this.feature = feature;
        this.config = config;
        showOnInventory();
        showOnBank();
    }

    @Override
    public void renderItemOverlay(Graphics2D graphics, int itemId, WidgetItem itemWidget) {
        if (!config.showItemOverlay()) {
            return;
        }

        if (itemId == ChuggingBarrelConstants.ITEM_ID) {
            graphics.setFont(FontManager.getRunescapeSmallFont());
            Rectangle bounds = itemWidget.getCanvasBounds();
            String selectedLoadoutName = feature.getSelectedLoadoutName();

            if (selectedLoadoutName == null || selectedLoadoutName.isEmpty()) {
                return;
            }

            String textToRender = selectedLoadoutName.length() > ChuggingBarrelConstants.OVERLAY_CHAR_LIMIT
                ? selectedLoadoutName.substring(0, ChuggingBarrelConstants.OVERLAY_CHAR_LIMIT)
                : selectedLoadoutName;

            FontMetrics fontMetrics = graphics.getFontMetrics();
            int textWidth = fontMetrics.stringWidth(textToRender);
            int xOffset = 4; // Barrel is slightly off center relative to its bounds
            int x = bounds.x + (bounds.width - xOffset - textWidth) / 2;
            int y = bounds.y + 22;

            TextComponent text = new TextComponent();
            text.setText(textToRender);
            text.setPosition(new Point(x, y));

            text.render(graphics);
        }
    }
}
