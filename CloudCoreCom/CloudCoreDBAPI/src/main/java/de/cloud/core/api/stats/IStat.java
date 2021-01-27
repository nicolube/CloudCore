package de.cloud.core.api.stats;

import de.cloud.core.api.usermanager.IUserInfo;

public interface IStat {

    long getId();

    IUserInfo getUserInfo();

    String getMode();

    String getName();

    long getValue();
}
