package de.lightfall.core.bukkit;

import com.j256.ormlite.jdbc.JdbcConnectionSource;
import de.lightfall.core.api.Util;
import de.lightfall.core.api.CoreAPI;
import de.lightfall.core.api.config.Config;
import de.lightfall.core.bukkit.usermanager.BukkitUserManager;
import lombok.Getter;
import lombok.SneakyThrows;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileReader;

public class MainBukkit extends JavaPlugin implements CoreAPI {
    @Getter
    private EventBasedExecutions eventBasedExecutions;
    private BukkitChannelHandler channelHandler;
    @Getter
    private BukkitUserManager userManager;
    @Getter
    private JdbcConnectionSource connectionSource;
    private Config config;

    @Override
    @SneakyThrows
    public void onEnable() {
        getLogger().info(Util.getLogo());

        getLogger().info("Copy necessary config files in to place...");
        final File configFile = new File(getDataFolder(), "config.json");
        if (!configFile.exists()) {
            getLogger().info("config.json copied");
            Util.copyOutOfJarFile("/resources/config.json", configFile);
        }
        getLogger().info("Loading configuration...");
        this.config = Util.getGSON().fromJson(new FileReader(configFile), Config.class);

        getLogger().info("Connect to database...");
        this.connectionSource = new JdbcConnectionSource(this.config.getDatabase().getUrl(),
                this.config.getDatabase().getUser(), this.config.getDatabase().getPassword());

        getLogger().info("Create Event based executor...");
        this.eventBasedExecutions = new EventBasedExecutions(this);
        getLogger().info("Create channel handler executor...");
        this.channelHandler = new BukkitChannelHandler(this);
        this.userManager = new BukkitUserManager(this);
    }
}
