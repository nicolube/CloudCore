package de.lightfall.core.api.punishments;

import java.util.Date;

public interface IPunishment {

    public long getId();
    public PunishmentType getType();
    public Date getCreated_at();
    public Date getEnd();
    public String getReason();
}
