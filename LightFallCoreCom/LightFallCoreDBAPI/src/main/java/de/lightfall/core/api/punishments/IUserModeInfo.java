package de.lightfall.core.api.punishments;

public interface IUserModeInfo {
    public long getId();

    public IUserInfo getUserInfo();

    public String getMode();

    public IPunishment getActiveBan();

    public IPunishment getActiveMute();
}
