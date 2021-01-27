package de.cloud.core.common;

import co.aikar.locales.MessageKeyProvider;
import com.j256.ormlite.dao.Dao;
import de.cloud.core.api.message.IMessageKeyProvider;
import de.cloud.core.common.models.MessageModel;
import lombok.SneakyThrows;

import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class MessageProvider {

    private final Dao<MessageModel, Long> messageDao;
    protected final HashMap<Locale, HashMap<MessageKeyProvider, String>> messages;
    private Logger logger;

    @SneakyThrows
    public MessageProvider(Dao<MessageModel, Long> messageDao, Logger logger) {
        this.logger = logger;
        this.messages = new HashMap<>();
        this.messageDao = messageDao;
    }

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
        final String prefix = translateAlternateColorCodes('&', prefixKey);
        mappedMessages.forEach((k, v) -> {
            v = translateAlternateColorCodes('&', v);
            this.messages.get(locale).put(k, v);
            if (k.hasPrefix())
                v = prefix + v;
            addMessage(locale, k, v);
        });
    }

    protected String translateAlternateColorCodes(char c, String prefixKey) {
        return prefixKey.replaceAll("&\\S", "");
    }

    protected native void addMessage(Locale locale, IMessageKeyProvider k, String v);

    public String getMessage(IMessageKeyProvider key, Locale locale) {
        final HashMap<MessageKeyProvider, String> providerStringHashMap = this.messages.get(locale);
        if (providerStringHashMap == null)
            return null;
        return providerStringHashMap.get(key);
    }
}
