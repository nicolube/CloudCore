package de.lightfall.core.bungee;

import co.aikar.commands.BungeeCommandManager;
import co.aikar.commands.MessageType;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.table.TableUtils;
import de.dytanic.cloudnet.driver.CloudNetDriver;
import de.dytanic.cloudnet.driver.service.GroupConfiguration;
import de.dytanic.cloudnet.ext.bridge.BridgePlayerManager;
import de.dytanic.cloudnet.ext.bridge.player.ICloudPlayer;
import de.lightfall.core.InternalCoreAPI;
import de.lightfall.core.MessageProvider;
import de.lightfall.core.api.Util;
import de.lightfall.core.api.channelhandeler.ChannelHandler;
import de.lightfall.core.api.channelhandeler.documents.ConfigRequestDocument;
import de.lightfall.core.api.config.Config;
import de.lightfall.core.api.message.CoreMessageKeys;
import de.lightfall.core.bungee.commands.*;
import de.lightfall.core.bungee.usermanager.BungeeCloudUser;
import de.lightfall.core.bungee.usermanager.BungeeUserManager;
import de.lightfall.core.models.PunishmentModel;
import de.lightfall.core.models.UserInfoModel;
import de.lightfall.core.models.UserModeInfoModel;
import lombok.Getter;
import lombok.SneakyThrows;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.plugin.Plugin;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.logging.Level;


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
    private MessageProvider messageProvider;

    private boolean enabled = false;
    @Getter
    private static MainBungee instance;
    @Getter
    private Dao<UserInfoModel, Long> userInfoDao;
    @Getter
    private Dao<UserModeInfoModel, Long> userModeInfoDao;
    @Getter
    private Dao<PunishmentModel,Long> punishmentDao;

    @Override
    @SneakyThrows
    public void onLoad() {
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
        Thread.sleep(1000);
        ChannelHandler.sendToCloud(new ConfigRequestDocument());
        Thread.sleep(1000);

        this.enabled = true;
        if (this.config != null) configure(this.config);
    }

    @SneakyThrows
    public void configure(Config config) {
        getLogger().info("Connect to database...");
        // System.setProperty(LocalLog.LOCAL_LOG_LEVEL_PROPERTY, "INFO");

        this.connectionSource = new JdbcConnectionSource(this.config.getDatabase().getUrl(),
                this.config.getDatabase().getUser(), this.config.getDatabase().getPassword());

        this.userInfoDao = DaoManager.createDao(this.connectionSource, UserInfoModel.class);
        this.userModeInfoDao = DaoManager.createDao(this.connectionSource, UserModeInfoModel.class);
        this.punishmentDao = DaoManager.createDao(this.connectionSource, PunishmentModel.class);
        TableUtils.createTableIfNotExists(this.connectionSource, UserInfoModel.class);
        TableUtils.createTableIfNotExists(this.connectionSource, UserModeInfoModel.class);
        TableUtils.createTableIfNotExists(this.connectionSource, PunishmentModel.class);

        getLogger().info("Starting command manager...");
        this.commandManager = new BungeeCommandManager(this);
        this.commandManager.usePerIssuerLocale(true);
        this.commandManager.getCommandCompletions().registerAsyncCompletion("taskGroup", context -> {
            Set<String> groups = new HashSet<>();
            CloudNetDriver.getInstance().getGroupConfigurationProvider().getGroupConfigurations().forEach(t -> groups.add(t.getName()));
            return groups;
        });
        this.commandManager.getCommandCompletions().registerAsyncCompletion("cloudPlayers", context ->
                BridgePlayerManager.getInstance().getOnlinePlayers().stream().map(ICloudPlayer::getName).collect(Collectors.toList()));


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
        this.messageProvider = new MessageProvider(this.connectionSource, this.commandManager, getLogger());
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
        this.commandManager.registerCommand(new UnbanCommand());
        this.commandManager.registerCommand(new UnmuteCommand());

        getLogger().info("Starting user manager...");
        this.userManager = new BungeeUserManager(this);
        getProxy().getPluginManager().registerListener(this, this.userManager);

        // Todo Move this to config
        Util.setBanFormat("&7● &bLightfall &7●\n§cDu bist vom Netzwerk gebannt!\n\n§7Grund §8» §e%1$s\n§7Ende des Banns §8» §e%2$s" +
                "\n\n§7Einen Entbannungsantrag kannst du im Forum schreiben.\n§7Forum §8» §aforum.lightfall.de\n" +
                "§7Teamspeak §8» §blightfall.de");
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
}
