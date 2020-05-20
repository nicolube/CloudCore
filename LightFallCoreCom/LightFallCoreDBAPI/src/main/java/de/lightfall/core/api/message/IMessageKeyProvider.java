package de.lightfall.core.api.message;

import co.aikar.locales.MessageKeyProvider;

public interface IMessageKeyProvider extends MessageKeyProvider {

    public String getMasterKey();
    public boolean hasPrefix();
}
