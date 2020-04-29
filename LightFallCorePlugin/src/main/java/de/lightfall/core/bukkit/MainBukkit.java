package de.lightfall.core.bukkit;

import com.j256.ormlite.jdbc.JdbcConnectionSource;
import de.lightfall.core.api.CoreAPI;
import de.lightfall.core.api.Util;
import de.lightfall.core.api.channelhandeler.ChannelHandler;
import de.lightfall.core.api.channelhandeler.documents.ConfigRequestDocument;
import de.lightfall.core.api.config.Config;
import de.lightfall.core.bukkit.usermanager.BukkitUserManager;
import lombok.Getter;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;

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
    public void onLoad() {
        getLogger().info("Create channel handler executor...");
        this.channelHandler = new BukkitChannelHandler(this);
        ChannelHandler.send(new ConfigRequestDocument());
    }

    @Override
    @SneakyThrows
    public void onEnable() {
        getLogger().info(Util.getLogo());

        // This is very sketchy
        final Field coreInstance = Util.class.getDeclaredField("coreInstance");
        coreInstance.setAccessible(true);
        coreInstance.set(null, this);

        if (this.config != null) configure(this.config);
    }

    @SneakyThrows
    private void configure(Config config) {
        getLogger().info("Connect to database...");
        this.connectionSource = new JdbcConnectionSource(this.config.getDatabase().getUrl(),
                this.config.getDatabase().getUser(), this.config.getDatabase().getPassword());

        getLogger().info("Create Event based executor...");
        this.eventBasedExecutions = new EventBasedExecutions(this);

        getLogger().info("Create user manager...");
        this.userManager = new BukkitUserManager(this);
        Bukkit.getPluginManager().registerEvents(this.userManager, this);
    }

    @SneakyThrows
    public void onConfigure(Config config) {
        this.config = config;
        if (isEnabled()) configure(config);
    }
}
