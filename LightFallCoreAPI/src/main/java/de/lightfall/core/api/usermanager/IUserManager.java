package de.lightfall.core.api.usermanager;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface IUserManager {

    /**
     * Gets the current {@link ICloudUser} singleton
     *
     * @param uuid of player you want to get.
     * @return ICloudUser or null if he is not login.
     */
    ICloudUser getUser(UUID uuid);


    /**
     * Gets a user from cached data or load the user from database {@link IOfflineCloudUser};
     * Returns {@link CompletableFuture} with {@link ICloudUser} when user is online,
     * {@link IOfflineCloudUser} when user is offline or null when he is not registered.
     *
     * @param uuid of player you want to get.
     * @return a CompletableFuture of IOfflineCloudUser
     */
    CompletableFuture<? extends IOfflineCloudUser> loadUser(UUID uuid);

}
