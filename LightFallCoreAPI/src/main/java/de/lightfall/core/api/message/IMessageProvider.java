package de.lightfall.core.api.message;

import co.aikar.commands.CommandManager;

import java.util.Locale;
import java.util.ResourceBundle;

public interface IMessageProvider {

    void registerMessageBundle(IMessageKeyProvider keyProvider, ResourceBundle bundle);

    void loadCommandManagerAsync(IMessageKeyProvider prefixKey, IMessageKeyProvider keyProvider, CommandManager commandManager);

    void loadCommandManager(IMessageKeyProvider prefixKey, IMessageKeyProvider keyProvider, CommandManager commandManager);

    String getMessage(IMessageKeyProvider key, Locale locale);
}
