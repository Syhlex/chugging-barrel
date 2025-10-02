package com.chuggingbarrel;

import com.chuggingbarrel.features.loadoutnames.LoadoutNames;
import com.chuggingbarrel.features.lowdoseindicator.LowDoseIndicator;
import com.chuggingbarrel.features.notbankedwarning.NotBankedWarning;
import com.chuggingbarrel.module.LifecycleComponentManager;
import com.chuggingbarrel.module.PluginLifecycleComponent;
import com.google.inject.Provides;

import net.runelite.client.config.ConfigManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

import java.util.List;

@PluginDescriptor(
    name = "Chugging Barrel"
)
public class ChuggingBarrelPlugin extends Plugin {
    private LifecycleComponentManager lifecycleComponentManager = null;

    @Provides
    ChuggingBarrelConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(ChuggingBarrelConfig.class);
    }

    @Provides
    public List<PluginLifecycleComponent> provideComponents(
        LoadoutNames loadoutNames,
        LowDoseIndicator lowDoseIndicator,
        NotBankedWarning notBankedWarning
    ) {
        return List.of(loadoutNames, lowDoseIndicator, notBankedWarning);
    }

    @Override
    protected void startUp() throws Exception {
        if (lifecycleComponentManager == null) {
            lifecycleComponentManager = injector.getInstance(LifecycleComponentManager.class);
        }
        lifecycleComponentManager.startUp();
    }

    @Override
    protected void shutDown() throws Exception {
        lifecycleComponentManager.shutDown();
    }
}
