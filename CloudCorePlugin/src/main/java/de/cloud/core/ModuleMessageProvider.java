package de.cloud.core;

import co.aikar.commands.CommandManager;
import co.aikar.commands.MessageType;
import co.aikar.locales.MessageKeyProvider;
import com.j256.ormlite.dao.Dao;
import de.cloud.core.api.message.IMessageKeyProvider;
import de.cloud.core.api.message.IMessageProvider;
import de.cloud.core.common.MessageProvider;
import de.cloud.core.common.models.MessageModel;
import lombok.SneakyThrows;
import net.md_5.bungee.api.ChatColor;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

public class ModuleMessageProvider extends MessageProvider implements IMessageProvider {

    private final InternalCoreAPI plugin;
    private Logger logger;

    @SneakyThrows
    public ModuleMessageProvider(Dao<MessageModel, Long> messageDao, InternalCoreAPI plugin, Logger logger) {
        super(messageDao, logger);
        this.plugin = plugin;
    }

    public void loadCommandManagerAsync(IMessageKeyProvider prefixKey, IMessageKeyProvider keyProvider, CommandManager commandManager) {
        CompletableFuture.runAsync(() -> loadCommandManager(prefixKey, keyProvider, commandManager));
    }

    @SneakyThrows
    public void loadCommandManager(IMessageKeyProvider prefixKey, IMessageKeyProvider keyProvider, CommandManager commandManager) {
        this.plugin.configChatColor(commandManager);
        IMessageKeyProvider[] keys = (IMessageKeyProvider[]) keyProvider.getClass().getDeclaredMethod("values").invoke(null);
        CommandManager pluginCommandManager = this.plugin.getCommandManager();
        final Set<Locale> supportedLanguages = pluginCommandManager.getSupportedLanguages();
        supportedLanguages.forEach(locale -> {
            String prefix = this.messages.get(locale).get(prefixKey);
            for (IMessageKeyProvider key : keys) {
                String message = this.messages.get(locale).get(key);
                if (key.hasPrefix())
                    message = prefix + message;
                commandManager.getLocales().addMessage(locale, key, message);
                pluginCommandManager.getLocales().addMessage(locale, key, message);
            }
        });
    }

    public String getMessage(IMessageKeyProvider key, Locale locale) {
        final HashMap<MessageKeyProvider, String> providerStringHashMap = this.messages.get(locale);
        if (providerStringHashMap == null)
            return null;
        return providerStringHashMap.get(key);
    }

    @Override
    protected void addMessage(Locale locale, IMessageKeyProvider k, String v) {
        this.plugin.getCommandManager().getLocales().addMessage(locale, k, v);
    }

    @Override
    protected String translateAlternateColorCodes(char c, String prefixKey) {
        return ChatColor.translateAlternateColorCodes(c, prefixKey);
    }
}
