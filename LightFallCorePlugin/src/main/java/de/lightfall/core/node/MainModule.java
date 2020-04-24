package de.lightfall.core.node;

import de.dytanic.cloudnet.driver.CloudNetDriver;
import de.dytanic.cloudnet.driver.module.ModuleLifeCycle;
import de.dytanic.cloudnet.driver.module.ModuleTask;
import de.dytanic.cloudnet.driver.module.driver.DriverModule;
import de.lightfall.core.Util;

public class MainModule extends DriverModule {

    @ModuleTask(event = ModuleLifeCycle.STARTED)
    public void registerListeners() {
        getLogger().info(Util.getLogo());
        CloudNetDriver.getInstance().getEventManager().registerListener(new ChannelListener());
    }
}
