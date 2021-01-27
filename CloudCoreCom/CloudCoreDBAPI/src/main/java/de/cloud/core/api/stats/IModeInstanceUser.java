package de.cloud.core.api.stats;

import de.cloud.core.api.usermanager.IUserInfo;

import java.util.Collection;

public interface IModeInstanceUser {

    long getId();

    IUserInfo getUserInfo();

    IModeInstance getModeInstance();

    Collection<? extends IStatUpdate> getStatUpdates();
}
