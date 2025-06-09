package com.chuggingbarrel.features.notbankedwarning;

import com.chuggingbarrel.ChuggingBarrelConfig;
import net.runelite.api.Client;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;
import net.runelite.client.ui.overlay.components.LineComponent;

import javax.inject.Inject;
import java.awt.*;

public class NotBankedOverlay extends OverlayPanel {
    private final Client client;
    private final ChuggingBarrelConfig config;

    @Inject
    NotBankedOverlay(Client client, ChuggingBarrelConfig config) {
        this.client = client;
        this.config = config;

        setPosition(OverlayPosition.ABOVE_CHATBOX_RIGHT);
        setLayer(OverlayLayer.UNDER_WIDGETS);
        setPriority(OverlayPriority.HIGH);
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        if (!config.showNotBankedWarning()) {
            return null;
        }

        panelComponent.getChildren().clear();

        panelComponent.getChildren().add(LineComponent.builder().left(config.notBankedWarningText()).build());

        panelComponent.setPreferredSize(this.getTextWidth(graphics, config.notBankedWarningText(), 8));

        if (client.getGameCycle() % 40 >= 20) {
            panelComponent.setBackgroundColor(config.notBankedWarningFlashColor1());
        } else {
            panelComponent.setBackgroundColor(config.notBankedWarningFlashColor2());
        }

        return panelComponent.render(graphics);
    }

    private Dimension getTextWidth(Graphics2D graphics, String string, int offset) {
        FontMetrics fontMetrics = graphics.getFontMetrics();
        int stringWidth = fontMetrics.stringWidth(string);
        return new Dimension(stringWidth + offset, 0);
    }
}
