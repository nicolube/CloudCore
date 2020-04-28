package de.lightfall.core.api.usermanager;

import de.dytanic.cloudnet.common.concurrent.ITask;
import de.dytanic.cloudnet.driver.service.ServiceInfoSnapshot;
import de.dytanic.cloudnet.driver.service.ServiceTask;
import de.dytanic.cloudnet.ext.bridge.player.ICloudPlayer;

import java.util.UUID;

public interface CloudUser {

    /**
     *
     * Moves the player to the server of a anther player and returns the
     * NetworkServiceInfo of the server where the player get moved to.
     *
     * @param player uuid of targetPlayer
     * @return NetworkServiceInfo or null if the player is offline.
     */
    public ITask<? extends ICloudPlayer> moveToPlayer(UUID player);


    /**
     * Works like {@link #moveToPlayer(UUID)}
     * and teleport the player on the target server to the target player
     *
     * @param uuid uuid of targetPlayer
     */
    public void moveToPlayerTeleport(UUID uuid);


    /**
     * Moves a player to a different service.
     *
     * @param service uuid of a cloudnet service
     */
    public void move(UUID service);

    /**
     * Works like {@link #move(UUID)}.
     *
     * @param service ServiceTask of service
     */
    public void move(ServiceTask service);

    /**
     * Works like {@link #move(UUID)}.
     *
     * @param service {@link ServiceInfoSnapshot} of service
     */
    public void move(ServiceInfoSnapshot service);

    /**
     * Works like {@link #move(UUID)}.
     *
     * @param service name of service
     */
    public void move(String service);

    /**
     * Gets the current {@link ICloudPlayer}
     *
     * @return ICloudPlayer of the current player
     */
    public ICloudPlayer getCloudPlayer();

    /**
     * Gets the current {@link UUID}
     *
     * @return UUID of the current player
     */
    public UUID getUuid();
}
