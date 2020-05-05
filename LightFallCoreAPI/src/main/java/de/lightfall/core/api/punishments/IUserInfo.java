package de.lightfall.core.api.punishments;

import java.util.UUID;

public interface IUserInfo {
    public long getId();

    public long getTeamspeak_id();

    public long getDiscord_id();

    public long getOntime();

    public UUID getUuid();

    public IPunishment getActiveBan();

    public IPunishment getActiveMute();

    public String getLocale();
}
