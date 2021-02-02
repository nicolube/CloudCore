package de.cloud.core.api.message;

import co.aikar.locales.MessageKey;
import lombok.Getter;

public enum CoreMessageKeys implements IMessageKeyProvider {
    PREFIX(false),
    CMD_KILL_TASK_NO_GROUP,
    CMD_KILL_TASK_STOPPED,
    CMD_CORE_RELOAD_MESSAGES,
    CMD_BAN_BANNED,
    CMD_BAN_DESCRIPTION(false),
    CMD_BAN_SYNTAX(false),
    CMD_BAN_LOWER_RANK,
    CMD_MUTE_MUTED,
    CMD_MUTE_DESCRIPTION(false),
    CMD_MUTE_SYNTAX(false),
    CMD_MUTE_LOWER_RANK,
    CMD_TEMPBAN_BANNED,
    CMD_TEMPBAN_DESCRIPTION(false),
    CMD_TEMPBAN_SYNTAX(false),
    CMD_TEMPMUTE_MUTED,
    CMD_TEMPMUTE_DESCRIPTION(false),
    CMD_TEMPMUTE_SYNTAX(false),
    CMD_UNBAN_UNBANNED,
    CMD_UNBAN_NOT_BANNED,
    CMD_UNBAN_SYNTAX(false),
    CMD_UNBAN_DESCRIPTION(false),
    CMD_UNMUTE_UNMUTED,
    CMD_UNMUTE_NOT_MUTED,
    CMD_UNMUTE_DESCRIPTION(false),
    CMD_UNMUTE_SYNTAX(false),
    CMD_KICK_SYNTAX(false),
    CMD_KICK_DESCRIPTION(false),
    CMD_KICK_KICKED,
    CMD_KICK_LOWER_RANK,
    CMD_PING_RESPONSE,
    CMD_PING_DESCRIPTION(false),
    MUTE,
    MUTED,
    TIMEFORMAT_FAIL;

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
        return MessageKey.of(masterKey + "." + name().toLowerCase());
    }

    @Override
    public boolean hasPrefix() {
        return this.prefix;
    }
}
