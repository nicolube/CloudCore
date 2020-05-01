package de.lightfall.core.api.message;

import java.util.ResourceBundle;

public interface IMessageProvider {

    public void registerMessageBundle(IMessageKeyProvider keyProvider, ResourceBundle bundle);
}
