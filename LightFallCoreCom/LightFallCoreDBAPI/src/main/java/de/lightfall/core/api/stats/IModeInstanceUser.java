package de.lightfall.core.api.stats;

import de.lightfall.core.api.usermanager.IUserInfo;

import java.util.Collection;

public interface IModeInstanceUser {

    long getId();

    IUserInfo getUserInfo();

    IModeInstance getModeInstance();

    Collection<IStatUpdate> getStatUpdates();
}
