package de.lightfall.core.api.usermanager;

import java.util.UUID;

public interface UserManager {

    /**
     *
     * Gets the current {@link CloudUser} singleton
     *
     * @param uuid of player you want to get.
     * @return CloudUser or null if he is not registered.
     */
    public CloudUser getUser(UUID uuid);
}
