package com.chuggingbarrel;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup(ChuggingBarrelConfig.GROUP)
public interface ChuggingBarrelConfig extends Config {
    String GROUP = "chuggingbarrel";
    String SELECTED_LOADOUT_INDEX = "SELECTED_LOADOUT_INDEX";

    @ConfigItem(
        keyName = "showItemOverlay",
        name = "Item Overlay",
        description = "Show the current loadout name over the chugging barrel (first " + ChuggingBarrelConstants.OVERLAY_CHAR_LIMIT + " characters)")
    default boolean showItemOverlay() {
        return true;
    }
}
