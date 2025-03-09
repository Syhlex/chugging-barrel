package com.chuggingbarrel.state;

import com.chuggingbarrel.ChuggingBarrelConfig;
import com.google.gson.Gson;
import net.runelite.client.config.ConfigManager;

import javax.inject.Inject;

public class ConfigStateManager {
    @Inject
    private Gson gson;

    @Inject
    private ConfigManager configManager;

    private final String[] POTION_INFO_KEYS = {
        "POTION_INFO_KEY_0",
        "POTION_INFO_KEY_1",
        "POTION_INFO_KEY_2",
        "POTION_INFO_KEY_3",
        "POTION_INFO_KEY_4"
    };

    public PotionInfo getPotionInfo(int index) {
        if (index < 0 || index > 4) {
            return null;
        }
        String potionInfoString = configManager.getRSProfileConfiguration(ChuggingBarrelConfig.GROUP, POTION_INFO_KEYS[index]);
        return gson.fromJson(potionInfoString, PotionInfo.class);
    }

    public void updatePotionInfo(int index, PotionInfo potionInfo) {
        if (index < 0 || index > 4) {
            return;
        }
        configManager.setRSProfileConfiguration(ChuggingBarrelConfig.GROUP, POTION_INFO_KEYS[index], gson.toJson(potionInfo));
    }
}
