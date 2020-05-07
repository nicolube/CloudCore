package de.lightfall.core.api.message;

import co.aikar.locales.MessageKey;
import lombok.Getter;

public enum CoreMessageKeys implements IMessageKeyProvider {
    PREFIX(false),
    CMD_KILL_TASK_NO_GROUP,
    CMD_KILL_TASK_STOPPED;

    private boolean prefix;

    CoreMessageKeys(boolean prefix) {
        this.prefix = prefix;
    }
    CoreMessageKeys() {
        this.prefix = true;
    }

    @Getter
    private String masterKey = "core";;

    @Override
    public MessageKey getMessageKey() {
        return MessageKey.of(masterKey+"."+name().toLowerCase());
    }

    @Override
    public boolean hasPrefix() {
        return this.prefix;
    }
}
