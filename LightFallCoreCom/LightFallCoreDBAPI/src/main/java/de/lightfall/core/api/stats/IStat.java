package de.lightfall.core.api.stats;

import de.lightfall.core.api.usermanager.IUserInfo;

public interface IStat {

    long getId();

    IUserInfo getUserInfo();

    String getMode();

    String getName();

    long getValue();
}
