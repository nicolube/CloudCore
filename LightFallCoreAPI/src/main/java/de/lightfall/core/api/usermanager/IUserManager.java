package de.lightfall.core.api.usermanager;

import java.util.UUID;

public interface IUserManager {

    /**
     *
     * Gets the current {@link ICloudUser} singleton
     *
     * @param uuid of player you want to get.
     * @return CloudUser or null if he is not registered.
     */
    public ICloudUser getUser(UUID uuid);
}
