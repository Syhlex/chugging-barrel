package com.chuggingbarrel.features.lowdoseindicator;

import com.chuggingbarrel.ChuggingBarrelConfig;
import com.chuggingbarrel.ChuggingBarrelConstants;
import net.runelite.api.widgets.WidgetItem;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.overlay.WidgetItemOverlay;
import net.runelite.client.ui.overlay.components.TextComponent;

import javax.inject.Inject;
import java.awt.*;

public class LowDoseOverlay extends WidgetItemOverlay {
    private final LowDoseIndicator feature;
    private final ChuggingBarrelConfig config;

    @Inject
    LowDoseOverlay(LowDoseIndicator feature, ChuggingBarrelConfig config) {
        this.feature = feature;
        this.config = config;
        showOnInventory();
        showOnBank();
    }


    public void renderItemOverlay(Graphics2D graphics, int itemId, WidgetItem itemWidget) {
        if (!config.showLowDoseOverlay() || !feature.isLowOnDoses()) {
            return;
        }

        if (itemId == ChuggingBarrelConstants.ITEM_ID) {
            graphics.setFont(FontManager.getRunescapeSmallFont());
            Rectangle bounds = itemWidget.getCanvasBounds();

            FontMetrics fontMetrics = graphics.getFontMetrics();
            int textWidth = fontMetrics.stringWidth(ChuggingBarrelConstants.LOW_DOSE_OVERLAY_TEXT);
            int x = bounds.x + (bounds.width - textWidth);
            int y = bounds.y + 36;

            TextComponent text = new TextComponent();
            text.setText(ChuggingBarrelConstants.LOW_DOSE_OVERLAY_TEXT);
            text.setPosition(new Point(x, y));
            text.setColor(Color.red);

            text.render(graphics);
        }
    }
}
