package de.lightfall.core.api.message;

import co.aikar.locales.MessageKey;
import lombok.Getter;

public enum CoreMessageKeys implements IMessageKeyProvider {
    PREFIX(false),
    CMD_KILL_TASK_NO_GROUP,
    BANNED_PLAYER,
    BANNED_PLAYER_PERMANENTLY,
    CMD_BAN_SYNTAX,
    TIMEFORMAT_FAIL,
    CMD_BAN_DESCRIPTION,
    MUTED_PLAYER,
    MUTED_PLAYER_PERMANENTLY,
    CMD_MUTE_SYNTAX,
    PLAYER_UNMUTED,
    PLAYER_NOT_MUTED,
    PLAYER_UNBANNED,
    PLAYER_NOT_BANNED,
    CMD_MUTE_DESCRIPTION,
    CMD_KILL_TASK_STOPPED,
    CMD_CORE_RELOAD_MESSAGES;

    private boolean prefix;

    CoreMessageKeys(boolean prefix) {
        this.prefix = prefix;
    }
    CoreMessageKeys() {
        this.prefix = true;
    }

    @Getter
    private String masterKey = "core";

    @Override
    public MessageKey getMessageKey() {
        return MessageKey.of(masterKey+"."+name().toLowerCase());
    }

    @Override
    public boolean hasPrefix() {
        return this.prefix;
    }
}
