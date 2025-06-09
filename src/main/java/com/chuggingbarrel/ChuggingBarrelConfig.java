package com.chuggingbarrel;

import net.runelite.client.config.*;

import java.awt.*;

@ConfigGroup(ChuggingBarrelConfig.GROUP)
public interface ChuggingBarrelConfig extends Config {
    String GROUP = "chuggingbarrel";
    String SELECTED_LOADOUT_INDEX = "SELECTED_LOADOUT_INDEX";

    @ConfigItem(
        keyName = "showItemOverlay",
        name = "Item Overlay",
        description = "Show the current loadout name over the chugging barrel (first " + ChuggingBarrelConstants.OVERLAY_CHAR_LIMIT + " characters)",
        position = 1
    )
    default boolean showItemOverlay() {
        return true;
    }

    @ConfigItem(
        keyName = "showLowDoseOverlay",
        name = "Low Dose Overlay",
        description = "Show an indicator on the item when a potion is low on doses",
        position = 2

    )
    default boolean showLowDoseOverlay() {
        return true;
    }

    @Range(min = 1, max = 99)
    @ConfigItem(
        keyName = "lowDoseThreshold",
        name = "Low Dose Threshold",
        description = "Low dose overlay shown when any potion doses fall below this amount",
        position = 3

    )
    default int lowDoseThreshold() {
        return 2;
    }

    @ConfigItem(
        keyName = "showNotBankedWarning",
        name = "Notify to bank item after use",
        description = "Upon drinking, show a warning until the item is no longer present in inventory",
        position = 4
    )
    default boolean showNotBankedWarning() {
        return true;
    }

    @ConfigItem(
        keyName = "notBankedWarningText",
        name = "Warning Text",
        description = "Text shown until item is banked after use",
        position = 5
    )
    default String notBankedWarningText() {
        return "Chugging barrel hasn't been banked after use!";
    }

    @ConfigItem(
        keyName = "notBankedWarningTimeout",
        name = "Warning Timeout",
        description = "The duration in seconds before the warning disappears",
        position = 6
    )
    default int notBankedWarningTimeout() {
        return 60;
    }

    @Alpha
    @ConfigItem(
        keyName = "notBankedWarningFlashColor1",
        name = "Flash Color #1",
        description = "First color the warning will flash between",
        position = 7
    )
    default Color notBankedWarningFlashColor1() {
        return new Color(0x96FF0000, true);
    }

    @Alpha
    @ConfigItem(
        keyName = "notBankedWarningFlashColor2",
        name = "Flash Color #2",
        description = "Second color the warning will flash between",
        position = 8
    )
    default Color notBankedWarningFlashColor2() {
        return new Color(0x96463D32, true);
    }
}
