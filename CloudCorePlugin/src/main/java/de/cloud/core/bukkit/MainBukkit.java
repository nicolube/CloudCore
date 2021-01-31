package de.cloud.core.bukkit;

import co.aikar.commands.MessageType;
import co.aikar.commands.PaperCommandManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import de.cloud.core.InternalCoreAPI;
import de.cloud.core.ModuleMessageProvider;
import de.cloud.core.api.ICorePlugin;
import de.cloud.core.api.Util;
import de.cloud.core.api.channelhandeler.ChannelHandler;
import de.cloud.core.api.channelhandeler.documents.ConfigRequestDocument;
import de.cloud.core.api.config.Config;
import de.cloud.core.api.message.CoreMessageKeys;
import de.cloud.core.bukkit.usermanager.BukkitUserManager;
import de.cloud.core.common.DatabaseProvider;
import de.dytanic.cloudnet.wrapper.Wrapper;
import lombok.Getter;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
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
    private String mode;
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
        this.databaseProvider = new DatabaseProvider(config.getDatabase());

        getLogger().info("Create Event based executor...");
        this.eventBasedExecutions = new EventBasedExecutions(this);

        getLogger().info("Starting command manager...");
        this.commandManager = new PaperCommandManager(this);
        this.commandManager.getLocales().setDefaultLocale(Locale.ENGLISH);

        getLogger().info("Loading messages...");
        this.messageProvider = new ModuleMessageProvider(databaseProvider.getMessageDao(), this.commandManager, getLogger());
        this.messageProvider.setColorConfig(config.getChatColorConfig());
        this.commandManager.getSupportedLanguages().clear();
        this.commandManager.addSupportedLanguage(Locale.GERMAN);
        this.commandManager.addSupportedLanguage(Locale.ENGLISH);
        loadMessages();

        getLogger().info("Create user manager...");
        this.userManager = new BukkitUserManager(this);
        Bukkit.getPluginManager().registerEvents(this.userManager, this);

        getLogger().info("Enable API plugins...");
        this.plugins.forEach(this::enableApiPlugin);
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

    @Override
    public void registerPlugin(ICorePlugin plugin) {
        this.plugins.add(plugin);
        if (this.config != null) {
            enableApiPlugin(plugin);
        }
    }

    @SneakyThrows
    private void enableApiPlugin(ICorePlugin plugin) {
        getLogger().info("Enable API-plugin: %s" + plugin.getName());
        plugin.onApiEnable();
    }
}
