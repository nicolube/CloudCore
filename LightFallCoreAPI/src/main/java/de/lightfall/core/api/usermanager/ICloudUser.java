package de.lightfall.core.api.usermanager;

import co.aikar.commands.MessageType;
import de.dytanic.cloudnet.common.concurrent.ITask;
import de.dytanic.cloudnet.driver.service.ServiceInfoSnapshot;
import de.dytanic.cloudnet.driver.service.ServiceTask;
import de.dytanic.cloudnet.ext.bridge.player.ICloudPlayer;
import de.lightfall.core.api.message.IMessageKeyProvider;

import java.util.UUID;

/**
 * Important: Do not cache any UserData!
 * 
 */
public interface ICloudUser extends IOfflineCloudUser {

    /**
     * Send a message to the player via a message kay you provided.
     *
     * @param type         {@link MessageType}
     * @param key          {@link IMessageKeyProvider}
     * @param replacements "key1", "value1", "key2", "value2"...
     */
    void sendMessage(MessageType type, IMessageKeyProvider key, String... replacements);

    /**
     * Moves the player to the server of a anther player and returns the
     * NetworkServiceInfo of the server where the player get moved to.
     *
     * @param player uuid of targetPlayer
     * @return NetworkServiceInfo or null if the player is offline.
     */
    ITask<? extends ICloudPlayer> moveToPlayer(UUID player);


    /**
     * Works like {@link #moveToPlayer(UUID)}
     * and teleport the player on the target server to the target player
     *
     * @param uuid uuid of targetPlayer
     */
    void moveToPlayerTeleport(UUID uuid);


    /**
     * Moves a player to a different service.
     *
     * @param service uuid of a cloudnet service
     */
    void move(UUID service);

    /**
     * Works like {@link #move(UUID)}.
     *
     * @param service ServiceTask of service
     */
    void move(ServiceTask service);

    /**
     * Works like {@link #move(UUID)}.
     *
     * @param service {@link ServiceInfoSnapshot} of service
     */
    void move(ServiceInfoSnapshot service);

    /**
     * Works like {@link #move(UUID)}.
     *
     * @param service name of service
     */
    void move(String service);

    /**
     * Gets the current {@link ICloudPlayer}
     *
     * @return ICloudPlayer of the current player
     */
    ICloudPlayer getCloudPlayer();

    /**
     * Returns name of CloudUser
     *
     * @return name of CloudUser
     */
    String getName();

    /**
     * Returns real name of CloudUser
     *
     * @return real name of CloudUser
     */
    String getRealName();


    /**
     * Returns current player, it can be either
     * {@link org.bukkit.entity.Player} or {@link net.md_5.bungee.api.connection.ProxiedPlayer}
     * Note: You cannot use this with offline load
     *
     * @param <T> valid Player type
     * @return valid Player
     */
    <T> T getPlayer();
}
