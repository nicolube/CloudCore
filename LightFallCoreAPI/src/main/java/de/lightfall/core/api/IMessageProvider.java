package de.lightfall.core.api;

import java.util.ResourceBundle;

public interface IMessageProvider {

    public void registerMessageBundle(IMessageKeyProvider keyProvider, ResourceBundle bundle);
}
