package com.chuggingbarrel.module;

import com.chuggingbarrel.features.loadoutnames.LoadoutNames;
import com.chuggingbarrel.features.lowdoseindicator.LowDoseIndicator;
import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;

public class ChuggingBarrelModule extends AbstractModule {
    @Override
    protected void configure() {
        Multibinder<PluginLifecycleComponent> lifecycleComponents = Multibinder.newSetBinder(binder(), PluginLifecycleComponent.class);
        lifecycleComponents.addBinding().to(LoadoutNames.class);
        lifecycleComponents.addBinding().to(LowDoseIndicator.class);
    }
}
