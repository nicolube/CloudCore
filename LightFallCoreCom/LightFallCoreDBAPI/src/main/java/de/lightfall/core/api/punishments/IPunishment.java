package de.lightfall.core.api.punishments;

import java.util.Date;

public interface IPunishment {

    public long getId();

    public IUserInfo getUserInfo();

    public IUserModeInfo getUserModeInfo();

    public IUserInfo getPunished_by();

    public PunishmentType getType();

    public Date getCreated_at();

    public Date getEnd();

    public String getReason();

    public IUserInfo getUnPunish_by();

    public Date getUnPunish_at();

    public String getUnPunish_comment();
}
