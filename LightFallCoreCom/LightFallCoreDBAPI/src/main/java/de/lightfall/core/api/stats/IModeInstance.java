package de.lightfall.core.api.stats;

import java.util.Collection;
import java.util.Date;

public interface IModeInstance {

    public long getId();

    public String getMode();

    Collection<IModeInstanceUser> getModeInstanceUsers();

    Collection<IModeInstanceData> getStatData();

    public Date getCreated_at();
}
