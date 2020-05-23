package de.lightfall.core.bukkit;

import co.aikar.commands.MessageType;
import co.aikar.commands.PaperCommandManager;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import de.dytanic.cloudnet.wrapper.Wrapper;
import de.lightfall.core.InternalCoreAPI;
import de.lightfall.core.ModuleMessageProvider;
import de.lightfall.core.api.Util;
import de.lightfall.core.api.channelhandeler.ChannelHandler;
import de.lightfall.core.api.channelhandeler.documents.ConfigRequestDocument;
import de.lightfall.core.api.config.Config;
import de.lightfall.core.api.message.CoreMessageKeys;
import de.lightfall.core.bukkit.usermanager.BukkitUserManager;
import de.lightfall.core.common.DatabaseProvider;
import de.lightfall.core.common.models.PunishmentModel;
import de.lightfall.core.common.models.UserInfoModel;
import de.lightfall.core.common.models.UserModeInfoModel;
import lombok.Getter;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Level;

public class MainBukkit extends JavaPlugin implements InternalCoreAPI {
    @Getter
    private EventBasedExecutions eventBasedExecutions;
    private BukkitChannelHandler channelHandler;
    @Getter
    private BukkitUserManager userManager;
    @Getter
    private JdbcConnectionSource connectionSource;
    private Config config;
    @Getter
    private PaperCommandManager commandManager;
    @Getter
    private ModuleMessageProvider messageProvider;
    @Getter
    private Dao<UserInfoModel, Long> userInfoDao;
    @Getter
    private Dao<UserModeInfoModel, Long> userModeInfoDao;
    @Getter
    private Dao<PunishmentModel,Long> punishmentDao;
    @Getter
    private String mode;

    @Override
    @SneakyThrows
    public void onLoad() {
        getLogger().setLevel(Level.FINER);
        getLogger().info(Util.getLogo());
        getLogger().info("Create channel handler executor...");
        this.channelHandler = new BukkitChannelHandler(this);
        ChannelHandler.sendToCloud(new ConfigRequestDocument());


        // This is very sketchy
        final Field coreInstance = Util.class.getDeclaredField("coreInstance");
        coreInstance.setAccessible(true);
        coreInstance.set(null, this);
        setMode(true);
    }

    @Override
    public void onEnable() {
        getLogger().info(Util.getLogo());
        if (this.config != null) configure(this.config);
    }

    @SneakyThrows
    private void configure(Config config) {
        getLogger().info("Connect to database...");
        //System.setProperty(LocalLog.LOCAL_LOG_LEVEL_PROPERTY, "INFO");
        DatabaseProvider databaseProvider = new DatabaseProvider(config.getDatabase());
        this.userInfoDao = databaseProvider.getUserInfoDao();
        this.userModeInfoDao = databaseProvider.getUserModeInfoDao();
        this.punishmentDao = databaseProvider.getPunishmentDao();

        getLogger().info("Create Event based executor...");
        this.eventBasedExecutions = new EventBasedExecutions(this);

        getLogger().info("Starting command manager...");
        this.commandManager = new PaperCommandManager(this);
        this.commandManager.getLocales().setDefaultLocale(Locale.ENGLISH);
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

        getLogger().info("Create user manager...");
        this.userManager = new BukkitUserManager(this);
        Bukkit.getPluginManager().registerEvents(this.userManager, this);
    }

    @SneakyThrows
    public void onConfigure(Config config) {
        this.config = config;
        if (isEnabled()) configure(config);
    }

    public void loadMessages() {
        this.messageProvider.registerMessageBundle(CoreMessageKeys.PREFIX, ResourceBundle.getBundle("core", Locale.GERMAN));
        this.messageProvider.registerMessageBundle(CoreMessageKeys.PREFIX, ResourceBundle.getBundle("core", Locale.ENGLISH));
    }

    @Override
    public void setMode(boolean mode) {
        this.mode = mode ? Wrapper.getInstance().getServiceId().getTaskName() : null;
    }
}
