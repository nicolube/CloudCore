package de.lightfall.core.api.message;

import co.aikar.commands.CommandManager;

import java.util.Locale;
import java.util.ResourceBundle;

public interface IMessageProvider {

    public void registerMessageBundle(IMessageKeyProvider keyProvider, ResourceBundle bundle);

    public void loadCommandManager(IMessageKeyProvider prefixKey, IMessageKeyProvider keyProvider, CommandManager commandManager);

    public String getMessage(IMessageKeyProvider key, Locale locale);
}
