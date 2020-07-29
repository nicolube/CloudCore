package de.lightfall.core.api.usermanager;

import de.lightfall.core.api.punishments.IPunishment;

public interface IUserModeInfo {
    public long getId();

    public IUserInfo getUserInfo();

    public String getMode();

    public IPunishment getActiveBan();

    public IPunishment getActiveMute();
}
