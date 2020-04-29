package de.lightfall.core.bungee;

import co.aikar.commands.BungeeCommandManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import de.lightfall.core.api.CoreAPI;
import de.lightfall.core.api.Util;
import de.lightfall.core.api.channelhandeler.ChannelHandler;
import de.lightfall.core.api.channelhandeler.documents.ConfigRequestDocument;
import de.lightfall.core.api.config.Config;
import de.lightfall.core.bungee.commands.KilltaskCommand;
import de.lightfall.core.bungee.commands.TestCommand;
import de.lightfall.core.bungee.usermanager.BungeeUserManager;
import lombok.Getter;
import lombok.SneakyThrows;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;

import java.lang.reflect.Field;


public class MainBungee extends Plugin implements CoreAPI {

    @Getter
    private BungeeUserManager userManager;
    private ChannelHandler channelHandler;
    private BungeeCommandManager commandManager;
    private Config config;
    @Getter
    private JdbcConnectionSource connectionSource;
    private boolean enabled = false;

    @Override
    public void onLoad() {
        getLogger().info("Create channel handler executor...");
        this.channelHandler = new BungeeChannelHandler(this);

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

        this.enabled = true;
        if (this.config != null) configure(this.config);
    }

    @SneakyThrows
    public void configure(Config config) {
        getLogger().info("Connect to database...");
        this.connectionSource = new JdbcConnectionSource(this.config.getDatabase().getUrl(),
                this.config.getDatabase().getUser(), this.config.getDatabase().getPassword());

        getLogger().info("Starting user manager...");
        this.userManager = new BungeeUserManager(this);
        getProxy().getPluginManager().registerListener(this, this.userManager);
        getLogger().info("Starting command manager...");
        this.commandManager = new BungeeCommandManager(this);
        getLogger().info("Registering commands...");
        // Todo remove Test command before release!
        this.commandManager.registerCommand(new TestCommand(this));
        ProxyServer.getInstance().getPluginManager().registerCommand(this,new KilltaskCommand("killtask"));
    }

    public void onConfigure(Config config) {
        this.config = config;
        if (enabled) configure(config);
    }
}
