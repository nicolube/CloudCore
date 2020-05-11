package de.lightfall.core.node;

import com.j256.ormlite.jdbc.JdbcConnectionSource;
import de.dytanic.cloudnet.driver.module.ModuleLifeCycle;
import de.dytanic.cloudnet.driver.module.ModuleTask;
import de.dytanic.cloudnet.driver.module.driver.DriverModule;
import de.lightfall.core.api.Util;
import de.lightfall.core.api.config.Config;
import lombok.Getter;
import lombok.SneakyThrows;

import java.io.File;
import java.io.FileReader;

public class MainModule extends DriverModule {

    @Getter
    private Config mConfig;
    private JdbcConnectionSource connectionSource;
    private NodeChannelHandler channelHandler;

    @SneakyThrows
    @ModuleTask(event = ModuleLifeCycle.LOADED)
    public void onEnable() {
        getLogger().info(Util.getLogo());

        getLogger().info("Copy necessary config files in to place...");
        final File configFile = new File(getModuleWrapper().getDataFolder(), "config.json");
        configFile.getParentFile().mkdirs();
        if (!configFile.exists()) {
            getLogger().info("config.json copied");
            Util.copyOutOfJarFile("/resources/config.json", configFile);
        }
        getLogger().info("Loading configuration...");
        this.mConfig = Util.getGSON().fromJson(new FileReader(configFile), Config.class);

        getLogger().info("Connect to database...");
        this.connectionSource = new JdbcConnectionSource(this.mConfig.getDatabase().getUrl(),
                this.mConfig.getDatabase().getUser(), this.mConfig.getDatabase().getPassword());

        getLogger().info("Create channel handler...");
        this.channelHandler = new NodeChannelHandler(this);
    }
}
