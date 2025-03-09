package com.chuggingbarrel;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.Range;

@ConfigGroup(ChuggingBarrelConfig.GROUP)
public interface ChuggingBarrelConfig extends Config {
    String GROUP = "chuggingbarrel";
    String SELECTED_LOADOUT_INDEX = "SELECTED_LOADOUT_INDEX";
    String POTION_DOSES = "POTION_DOSES";

    @ConfigItem(
        keyName = "showItemOverlay",
        name = "Item Overlay",
        description = "Show the current loadout name over the chugging barrel (first " + ChuggingBarrelConstants.OVERLAY_CHAR_LIMIT + " characters)")
    default boolean showItemOverlay() {
        return true;
    }

    @ConfigItem(
        keyName = "showLowDoseOverlay",
        name = "Low Dose Overlay",
        description = "Show an indicator on the item when a potion is low on doses"
    )
    default boolean showLowDoseOverlay() {
        return true;
    }

    @Range(min = 1, max = 99)
    @ConfigItem(
        keyName = "lowDoseThreshold",
        name = "Low Dose Threshold",
        description = "Low dose overlay shown when any potion doses fall below this amount"
    )
    default int lowDoseThreshold() {
        return 2;
    }
}
