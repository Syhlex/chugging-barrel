package com.chuggingbarrel.module;

import javax.inject.Inject;
import java.util.Set;

public class LifecycleComponentManager {
    private final Set<PluginLifecycleComponent> lifecycleComponents;

    @Inject
    LifecycleComponentManager(Set<PluginLifecycleComponent> lifecycleComponents) {
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
