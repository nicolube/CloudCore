package de.lightfall.core.api;

import co.aikar.locales.MessageKeyProvider;

public interface IMessageKeyProvider extends MessageKeyProvider {

    public String getMasterKey();
    public boolean hasPrefix();
}
