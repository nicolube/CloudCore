package de.lightfall.core.api.stats;

import java.util.Collection;

public interface IModeInstanceUser {

    IModeInstance getModeInstance();

    Collection<IStatUpdate> getStatUpdates();
}
