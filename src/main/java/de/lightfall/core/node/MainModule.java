package de.lightfall.core.node;

import de.dytanic.cloudnet.driver.CloudNetDriver;
import de.dytanic.cloudnet.driver.module.ModuleLifeCycle;
import de.dytanic.cloudnet.driver.module.ModuleTask;
import de.dytanic.cloudnet.driver.module.driver.DriverModule;

public class MainModule extends DriverModule {

    @ModuleTask(event = ModuleLifeCycle.STARTED)
    public void onStart() {

    }

    @ModuleTask(event = ModuleLifeCycle.STARTED) //important section, because on this event the module will start
    public void registerListeners() {
        CloudNetDriver.getInstance().getEventManager().registerListener(new ChannelListener());
    }

}
