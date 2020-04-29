package de.lightfall.core.bungee;

import co.aikar.commands.BungeeCommandManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import de.dytanic.cloudnet.driver.CloudNetDriver;
import de.dytanic.cloudnet.driver.service.GroupConfiguration;
import de.lightfall.core.api.CoreAPI;
import de.lightfall.core.api.Util;
import de.lightfall.core.api.channelhandeler.ChannelHandler;
import de.lightfall.core.api.channelhandeler.documents.ConfigRequestDocument;
import de.lightfall.core.api.config.Config;
import de.lightfall.core.bungee.commands.KillTaskCommand;
import de.lightfall.core.bungee.commands.TestCommand;
import de.lightfall.core.bungee.usermanager.BungeeUserManager;
import lombok.Getter;
import lombok.SneakyThrows;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;


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

    }

    @Override
    @SneakyThrows
    public void onEnable() {
        getLogger().info(Util.getLogo());

        // This is very sketchy
        final Field coreInstance = Util.class.getDeclaredField("coreInstance");
        coreInstance.setAccessible(true);
        coreInstance.set(null, this);

        ChannelHandler.send(new ConfigRequestDocument());

        Thread.sleep(1000);

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

        this.commandManager.getCommandCompletions().registerAsyncCompletion("taskGroup", context -> {
            Set<String> groups = new HashSet<>();
            CloudNetDriver.getInstance().getGroupConfigurationProvider().getGroupConfigurations().forEach(t -> groups.add(t.getName()));
            return groups;
        });
        this.commandManager.getCommandContexts().registerContext(GroupConfiguration.class, context -> {
            return CloudNetDriver.getInstance().getGroupConfigurationProvider().getGroupConfiguration(context.popFirstArg());
        });

        // Todo remove Test command before release!
        this.commandManager.registerCommand(new TestCommand(this));
        this.commandManager.registerCommand(new KillTaskCommand());
    }

    public void onConfigure(Config config) {
        this.config = config;
        if (enabled) configure(config);
    }
}
