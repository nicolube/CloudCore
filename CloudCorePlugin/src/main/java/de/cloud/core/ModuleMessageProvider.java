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

    private final CommandManager commandManager;
    private Logger logger;
    private Map<String, String[]> chatColorConfig;

    @SneakyThrows
    public ModuleMessageProvider(Dao<MessageModel, Long> messageDao, CommandManager commandManager, Logger logger) {
        super(messageDao, logger);
        this.commandManager = commandManager;
    }

    public void loadCommandManagerAsync(IMessageKeyProvider prefixKey, IMessageKeyProvider keyProvider, CommandManager commandManager) {
        CompletableFuture.runAsync(() -> loadCommandManager(prefixKey, keyProvider, commandManager));
    }

    @SneakyThrows
    public void loadCommandManager(IMessageKeyProvider prefixKey, IMessageKeyProvider keyProvider, CommandManager commandManager) {
        chatColorConfig.forEach((t, c) -> {
            final org.bukkit.ChatColor[] chatColors = new org.bukkit.ChatColor[c.length];
            for (int i = 0; i < c.length; i++) chatColors[i] = org.bukkit.ChatColor.valueOf(c[i].toUpperCase());
            try {
                MessageType type = (MessageType) MessageType.class.getDeclaredField(t).get(null);
                this.commandManager.setFormat(type, chatColors);
            } catch (IllegalAccessException | NoSuchFieldException e) {
                e.printStackTrace();
            }
        });

        IMessageKeyProvider[] keys = (IMessageKeyProvider[]) keyProvider.getClass().getDeclaredMethod("values").invoke(null);
        final Set<Locale> supportedLanguages = this.commandManager.getSupportedLanguages();
        supportedLanguages.forEach(locale -> {
            for (IMessageKeyProvider key : keys) {
                String message = this.messages.get(locale).get(key);
                if (key.hasPrefix())
                    message = this.messages.get(locale).get(prefixKey) + message;
                commandManager.getLocales().addMessage(locale, key, message);
                this.commandManager.getLocales().addMessage(locale, key, message);
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
        this.commandManager.getLocales().addMessage(locale, k, v);
    }

    @Override
    protected String translateAlternateColorCodes(char c, String prefixKey) {
        return ChatColor.translateAlternateColorCodes(c, prefixKey);
    }

    public void setColorConfig(Map<String, String[]> chatColorConfig) {
        this.chatColorConfig = chatColorConfig;
    }
}
