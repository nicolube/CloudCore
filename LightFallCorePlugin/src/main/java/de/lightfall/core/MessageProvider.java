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

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

public class MessageProvider implements IMessageProvider {

    private final Dao<MessageModel, Long> messageDao;
    private final CommandManager commandManager;
    private Logger logger;

    @SneakyThrows
    public MessageProvider(ConnectionSource connectionSource, CommandManager commandManager, Logger logger) {
        this.logger = logger;
        this.commandManager = commandManager;
        this.messageDao = DaoManager.createDao(connectionSource, MessageModel.class);
        TableUtils.createTableIfNotExists(connectionSource, MessageModel.class);
    }

    @Override
    public void registerMessageBundle(IMessageKeyProvider keyProvider, ResourceBundle bundle) {
        CompletableFuture.runAsync(() -> {
            MessageKeyProvider[] keys = null;
            try {
                keys = (MessageKeyProvider[]) keyProvider.getClass().getDeclaredMethod("values").invoke(null);
            } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                e.printStackTrace();
            }
            for (MessageKeyProvider key : keys) {
                String keyString = key.getMessageKey().getKey();
                try {
                    final String message = bundle.getString(keyString);
                    this.messageDao.create(new MessageModel(keyString, message, bundle.getLocale()));
                    this.logger.info(String.format("%s(%s) inserted into message db", keyString, bundle.getLocale().getLanguage()));
                } catch (MissingResourceException | SQLException ex) {
                    this.logger.info(String.format("%s(%s) already exist in message db.", keyString, bundle.getLocale().getLanguage()));
                }
            }
            load(keyProvider, bundle.getLocale());
        });
    }

    public void load(IMessageKeyProvider keyProvider, Locale locale) {
        CompletableFuture.runAsync(() -> {
            final List<MessageModel> queried;
            try {
                queried = this.messageDao.queryBuilder().where().eq("locale", locale.getLanguage())
                        .and().like("key", keyProvider.getMasterKey() + "%").query();
                final HashMap<IMessageKeyProvider, String> mappedMessages = new HashMap<>();
                IMessageKeyProvider[] keys = (IMessageKeyProvider[]) keyProvider.getClass().getDeclaredMethod("values").invoke(null);
                queried.forEach(messageModel -> {
                    for (IMessageKeyProvider key : keys) {
                        if (key.getMessageKey().getKey().equals(messageModel.getKey())) {
                            logger.info("Found");
                            mappedMessages.put(key, messageModel.getMessage());
                            break;
                        }
                    }
                });
                final String prefixKey = mappedMessages.remove(keyProvider);
                final String prefix = ChatColor.translateAlternateColorCodes('&', prefixKey);
                mappedMessages.forEach((k, v) -> {
                    if (k.hasPrefix()) {
                        this.commandManager.getLocales().addMessage(locale, k, prefix + v);
                        return;
                    }
                    this.commandManager.getLocales().addMessage(locale, k, v);
                });
            } catch (SQLException | NoSuchMethodException | IllegalAccessException | InvocationTargetException ex) {
                ex.printStackTrace();
            }
        });
    }
}
