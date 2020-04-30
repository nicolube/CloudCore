package de.lightfall.core.bungee;

import co.aikar.commands.BungeeCommandManager;
import co.aikar.commands.MessageType;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.logger.LocalLog;
import de.dytanic.cloudnet.driver.CloudNetDriver;
import de.dytanic.cloudnet.driver.service.GroupConfiguration;
import de.lightfall.core.api.CoreMessageKeys;
import de.lightfall.core.MessageProvider;
import de.lightfall.core.api.CoreAPI;
import de.lightfall.core.api.Util;
import de.lightfall.core.api.channelhandeler.ChannelHandler;
import de.lightfall.core.api.channelhandeler.documents.ConfigRequestDocument;
import de.lightfall.core.api.config.Config;
import de.lightfall.core.bungee.commands.TestCommand;
import de.lightfall.core.bungee.usermanager.BungeeUserManager;
import lombok.Getter;
import lombok.SneakyThrows;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.plugin.Plugin;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Set;


public class MainBungee extends Plugin implements CoreAPI {

    @Getter
    private BungeeUserManager userManager;
    private ChannelHandler channelHandler;
    @Getter
    private BungeeCommandManager commandManager;
    private Config config;
    @Getter
    private JdbcConnectionSource connectionSource;
    @Getter
    private MessageProvider messageProvider;

    private boolean enabled = false;
    @Getter
    private static MainBungee instance;

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
        this.instance = this;

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
        System.setProperty(LocalLog.LOCAL_LOG_LEVEL_PROPERTY, "INFO");

        getLogger().info("Starting user manager...");
        this.userManager = new BungeeUserManager(this);
        getProxy().getPluginManager().registerListener(this, this.userManager);
        getLogger().info("Starting command manager...");
        this.commandManager = new BungeeCommandManager(this);

        this.commandManager.getCommandCompletions().registerAsyncCompletion("taskGroup", context -> {
            Set<String> groups = new HashSet<>();
            CloudNetDriver.getInstance().getGroupConfigurationProvider().getGroupConfigurations().forEach(t -> groups.add(t.getName()));
            return groups;
        });
        this.commandManager.getCommandContexts().registerContext(GroupConfiguration.class, context -> {
            return CloudNetDriver.getInstance().getGroupConfigurationProvider().getGroupConfiguration(context.popFirstArg());
        });

        config.getChatColorConfig().forEach((t, c) -> {
            final ChatColor[] chatColors = new ChatColor[c.length];
            for (int i = 0; i < c.length; i++) chatColors[i] = ChatColor.valueOf(c[i].toUpperCase());
            try {
                MessageType type = (MessageType) MessageType.class.getDeclaredField(t).get(null);
                this.commandManager.setFormat(type, chatColors);
            } catch (IllegalAccessException | NoSuchFieldException e) {
                e.printStackTrace();
            }
        });

        getLogger().info("Loading messages...");
        this.messageProvider = new MessageProvider(this.connectionSource, this.commandManager, getLogger());
        this.commandManager.getSupportedLanguages().clear();
        this.commandManager.addSupportedLanguage(Locale.GERMAN);
        this.commandManager.addSupportedLanguage(Locale.ENGLISH);
        this.messageProvider.registerMessageBundle(CoreMessageKeys.PREFIX, ResourceBundle.getBundle("core", Locale.GERMAN));
        this.messageProvider.registerMessageBundle(CoreMessageKeys.PREFIX, ResourceBundle.getBundle("core", Locale.ENGLISH));

        getLogger().info("Registering commands...");
        // Todo remove Test command before release!
        this.commandManager.registerCommand(new TestCommand(this));
        //this.commandManager.registerCommand(new KillTaskCommand());
    }

    public void onConfigure(Config config) {
        this.config = config;
        if (enabled) configure(config);
    }
}
