package de.cloud.core.bungee;

import co.aikar.commands.BungeeCommandManager;
import co.aikar.commands.MessageType;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import de.cloud.core.InternalCoreAPI;
import de.cloud.core.ModuleMessageProvider;
import de.cloud.core.api.ICorePlugin;
import de.cloud.core.api.Util;
import de.cloud.core.api.channelhandeler.ChannelHandler;
import de.cloud.core.api.channelhandeler.documents.ConfigRequestDocument;
import de.cloud.core.api.config.Config;
import de.cloud.core.api.message.CoreMessageKeys;
import de.cloud.core.bungee.commands.*;
import de.cloud.core.bungee.usermanager.BungeeCloudUser;
import de.cloud.core.bungee.usermanager.BungeeUserManager;
import de.cloud.core.common.DatabaseProvider;
import de.dytanic.cloudnet.CloudNet;
import de.dytanic.cloudnet.driver.CloudNetDriver;
import de.dytanic.cloudnet.driver.service.GroupConfiguration;
import de.dytanic.cloudnet.ext.bridge.player.ICloudPlayer;
import de.dytanic.cloudnet.ext.bridge.player.IPlayerManager;
import lombok.Getter;
import lombok.SneakyThrows;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.plugin.Plugin;

import java.lang.reflect.Field;
import java.util.*;
import java.util.logging.Level;
import java.util.stream.Collectors;


public class MainBungee extends Plugin implements InternalCoreAPI {

    @Getter
    private BungeeUserManager userManager;
    private ChannelHandler channelHandler;
    @Getter
    private BungeeCommandManager commandManager;
    private Config config;
    @Getter
    private JdbcConnectionSource connectionSource;
    @Getter
    private ModuleMessageProvider messageProvider;

    private boolean enabled = false;
    @Getter
    private static MainBungee instance;
    @Getter
    private DatabaseProvider databaseProvider;
    private List<ICorePlugin> plugins;

    @Override
    @SneakyThrows
    public void onLoad() {
        this.plugins = new ArrayList<>();
        getLogger().setLevel(Level.FINER);
        getLogger().info(Util.getLogo());
        getLogger().info("Create channel handler executor...");
        this.channelHandler = new BungeeChannelHandler(this);

        // This is very sketchy
        final Field coreInstance = Util.class.getDeclaredField("coreInstance");
        coreInstance.setAccessible(true);
        coreInstance.set(null, this);
        instance = this;

    }

    @Override
    @SneakyThrows
    public void onEnable() {
        getLogger().info(Util.getLogo());
        ChannelHandler.sendToCloud(new ConfigRequestDocument());

        this.enabled = true;
        if (this.config != null) configure(this.config);
    }

    @SneakyThrows
    public void configure(Config config) {
        getLogger().info("Connect to database...");
        // System.setProperty(LocalLog.LOCAL_LOG_LEVEL_PROPERTY, "INFO");
        this.databaseProvider = new DatabaseProvider(config.getDatabase());

        getLogger().info("Starting command manager...");
        this.commandManager = new BungeeCommandManager(this);
        this.commandManager.usePerIssuerLocale(true);
        this.commandManager.getCommandCompletions().registerAsyncCompletion("taskGroup", context -> {
            Set<String> groups = new HashSet<>();
            CloudNetDriver.getInstance().getGroupConfigurationProvider().getGroupConfigurations().forEach(t -> groups.add(t.getName()));
            return groups;
        });
        IPlayerManager playerManager = CloudNet.getInstance().getServicesRegistry().getFirstService(IPlayerManager.class);
        this.commandManager.getCommandCompletions().registerAsyncCompletion("cloudPlayers", context ->
                playerManager.getOnlinePlayers().stream().map(ICloudPlayer::getName).collect(Collectors.toList()));


        this.commandManager.getCommandContexts().registerContext(GroupConfiguration.class, context ->
                CloudNetDriver.getInstance().getGroupConfigurationProvider().getGroupConfiguration(context.popFirstArg()));
        this.commandManager.getCommandContexts().registerIssuerOnlyContext(BungeeCloudUser.class, ioc -> this.userManager.getUser(ioc.getPlayer().getUniqueId()));

        getLogger().info("Configure ChatColor...");
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
        this.messageProvider = new ModuleMessageProvider(databaseProvider.getMessageDao(), this.commandManager, getLogger());
        this.commandManager.getSupportedLanguages().clear();
        this.commandManager.addSupportedLanguage(Locale.GERMAN);
        this.commandManager.addSupportedLanguage(Locale.ENGLISH);
        loadMessages();

        getLogger().info("Registering commands...");
        // Todo remove Test command before release!
        this.commandManager.registerCommand(new TestCommand(this));
        this.commandManager.registerCommand(new CoreCommand(this));
        this.commandManager.registerCommand(new MuteCommand(this));
        this.commandManager.registerCommand(new BanCommand(this));
        this.commandManager.registerCommand(new TempbanCommand(this));
        this.commandManager.registerCommand(new TempmuteCommand(this));
        this.commandManager.registerCommand(new KickCommand(this));
        this.commandManager.registerCommand(new UnbanCommand());
        this.commandManager.registerCommand(new UnmuteCommand());
        this.commandManager.registerCommand(new PingCommand());

        getLogger().info("Starting user manager...");
        this.userManager = new BungeeUserManager(this);
        getProxy().getPluginManager().registerListener(this, this.userManager);

        // Todo Move this to config
        Util.setBanFormat("&7● &bCloudCore &7●\n§cDu bist vom Netzwerk gebannt!\n\n§7Grund §8» §e%1$s\n§7Ende des Banns §8» §e%2$s" +
                "\n\n§7Einen Entbannungsantrag kannst du im Forum schreiben.\n§7Forum §8» §aforum.lightfall.de\n" +
                "§7Teamspeak §8» §blightfall.de");


        getLogger().info("Enable API plugins...");
        this.plugins.forEach(this::enableApiPlugin);
    }

    public void onConfigure(Config config) {
        this.config = config;
        if (enabled) configure(config);
    }

    public void loadMessages() {
        this.messageProvider.registerMessageBundle(CoreMessageKeys.PREFIX, ResourceBundle.getBundle("core", Locale.GERMAN));
        this.messageProvider.registerMessageBundle(CoreMessageKeys.PREFIX, ResourceBundle.getBundle("core", Locale.ENGLISH));
    }

    @Override
    public native void setMode(boolean mode);

    @Override
    public void registerPlugin(ICorePlugin plugin) {
        this.plugins.add(plugin);
        if (config != null) {
            enableApiPlugin(plugin);
        }
    }

    @SneakyThrows
    private void enableApiPlugin(ICorePlugin plugin) {
        getLogger().info("Enable API-plugin: %s" + plugin.getName());
        plugin.onApiEnable();
    }
}
