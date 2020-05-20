package de.lightfall.core;

import co.aikar.commands.CommandManager;
import co.aikar.locales.MessageKeyProvider;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import de.lightfall.core.api.message.IMessageKeyProvider;
import de.lightfall.core.api.message.IMessageProvider;
import de.lightfall.core.models.MessageModel;
import lombok.SneakyThrows;
import net.md_5.bungee.api.ChatColor;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class MessageProvider implements IMessageProvider {

    private final Dao<MessageModel, Long> messageDao;
    private final CommandManager commandManager;
    private final HashMap<Locale, HashMap<MessageKeyProvider, String>> messages;
    private Logger logger;

    @SneakyThrows
    public MessageProvider(ConnectionSource connectionSource, CommandManager commandManager, Logger logger) {
        this.logger = logger;
        this.commandManager = commandManager;
        this.messages = new HashMap<>();
        this.messageDao = DaoManager.createDao(connectionSource, MessageModel.class);
        TableUtils.createTableIfNotExists(connectionSource, MessageModel.class);
    }

    @Override
    @SneakyThrows
    public void registerMessageBundle(IMessageKeyProvider keyProvider, ResourceBundle bundle) {
        MessageKeyProvider[] keys = null;
        keys = (MessageKeyProvider[]) keyProvider.getClass().getDeclaredMethod("values").invoke(null);
        List<String> exitingKeys = this.messageDao.queryBuilder().where().eq("locale", bundle.getLocale().getLanguage())
                .and().like("key", keyProvider.getMasterKey() + "%").query().stream().map(MessageModel::getKey).collect(Collectors.toList());
        for (MessageKeyProvider key : keys) {
            String keyString = key.getMessageKey().getKey();
                final String message = bundle.getString(keyString);
                if (exitingKeys.contains(keyString)) {
                    this.logger.info(String.format("%s(%s) already exist in message db.", keyString, bundle.getLocale().getLanguage()));
                    continue;
                }
                this.messageDao.create(new MessageModel(keyString, message, bundle.getLocale()));
                this.logger.info(String.format("%s(%s) inserted into message db", keyString, bundle.getLocale().getLanguage()));
        }
        load(keyProvider, bundle.getLocale());
    }

    @SneakyThrows
    public void load(IMessageKeyProvider keyProvider, Locale locale) {
        if (!this.messages.containsKey(locale))
            this.messages.put(locale, new HashMap<>());
        List<MessageModel> queried = this.messageDao.queryBuilder().where().eq("locale", locale.getLanguage())
                .and().like("key", keyProvider.getMasterKey() + "%").query();
        final HashMap<IMessageKeyProvider, String> mappedMessages = new HashMap<>();
        IMessageKeyProvider[] keys = (IMessageKeyProvider[]) keyProvider.getClass().getDeclaredMethod("values").invoke(null);
        queried.forEach(messageModel -> {
            for (IMessageKeyProvider key : keys) {
                if (key.getMessageKey().getKey().equals(messageModel.getKey())) {
                    mappedMessages.put(key, messageModel.getMessage());
                    break;
                }
            }
        });
        final String prefixKey = mappedMessages.remove(keyProvider);
        final String prefix = ChatColor.translateAlternateColorCodes('&', prefixKey);
        mappedMessages.forEach((k, v) -> {
            v = ChatColor.translateAlternateColorCodes('&', v);
            this.messages.get(locale).put(k, v);
            if (k.hasPrefix())
                v = prefix + v;
            this.commandManager.getLocales().addMessage(locale, k, v);
        });
    }

    public void loadCommandManagerAsync(IMessageKeyProvider prefixKey, IMessageKeyProvider keyProvider, CommandManager commandManager){
        CompletableFuture.runAsync(() -> loadCommandManager(prefixKey, keyProvider, commandManager));
    }

    @SneakyThrows
    public void loadCommandManager(IMessageKeyProvider prefixKey, IMessageKeyProvider keyProvider, CommandManager commandManager) {
        IMessageKeyProvider[] keys = (IMessageKeyProvider[]) keyProvider.getClass().getDeclaredMethod("values").invoke(null);
        final Set<Locale> supportedLanguages = this.commandManager.getSupportedLanguages();
        supportedLanguages.forEach(locale -> {
            for (IMessageKeyProvider key : keys) {
                String message = this.messages.get(locale).get(key);
                if (key.hasPrefix())
                    message = this.messages.get(locale).get(prefixKey)+message;
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
}
