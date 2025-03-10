package com.chuggingbarrel;

import com.chuggingbarrel.module.ChuggingBarrelModule;
import com.chuggingbarrel.module.LifecycleComponentManager;
import com.google.inject.Binder;
import com.google.inject.Provides;

import net.runelite.client.config.ConfigManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

@PluginDescriptor(
    name = "Chugging Barrel"
)
public class ChuggingBarrelPlugin extends Plugin {
    private LifecycleComponentManager lifecycleComponentManager = null;

    @Override
    public void configure(Binder binder) {
        binder.install(new ChuggingBarrelModule());
    }

    @Provides
    ChuggingBarrelConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(ChuggingBarrelConfig.class);
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
