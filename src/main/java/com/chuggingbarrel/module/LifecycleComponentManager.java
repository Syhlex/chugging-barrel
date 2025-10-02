package com.chuggingbarrel.module;

import javax.inject.Inject;
import java.util.List;

public class LifecycleComponentManager {
    private final List<PluginLifecycleComponent> lifecycleComponents;

    @Inject
    LifecycleComponentManager(List<PluginLifecycleComponent> lifecycleComponents) {
        this.lifecycleComponents = lifecycleComponents;
    }

    public void startUp() {
        for (PluginLifecycleComponent lifecycleComponent : lifecycleComponents) {
            lifecycleComponent.startup();
        }
    }

    public void shutDown() {
        for (PluginLifecycleComponent lifecycleComponent : lifecycleComponents) {
            lifecycleComponent.shutdown();
        }
    }
}
