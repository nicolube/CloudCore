package de.lightfall.core.api.usermanager;

import co.aikar.commands.MessageType;
import de.dytanic.cloudnet.common.concurrent.ITask;
import de.dytanic.cloudnet.driver.service.ServiceInfoSnapshot;
import de.dytanic.cloudnet.driver.service.ServiceTask;
import de.dytanic.cloudnet.ext.bridge.player.ICloudPlayer;
import de.lightfall.core.api.message.IMessageKeyProvider;

import java.util.Locale;
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
    public void sendMessage(MessageType type, IMessageKeyProvider key, String... replacements);

    /**
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
     * Returns name of CloudUser
     *
     * @return name of CloudUser
     */
    public String getName();

    /**
     * Returns real name of CloudUser
     *
     * @return real name of CloudUser
     */
    public String getRealName();

    /**
     * Banns a user permanent.
     *
     * @param sender user who executed the punishment or null for console
     * @param mode   mode name or null for global context
     * @param reason why the user was punished
     */
    public void ban(ICloudUser sender, String mode, String reason);

    /**
     * Banns a user temporary.
     *
     * @param sender user who executed the punishment or null for console
     * @param mode   mode name or null for global context
     * @param length length of punishment in seconds
     * @param reason why the user was punished
     */
    public void tempBan(ICloudUser sender, String mode, long length, String reason);

    /**
     * Mutes a user permanent.
     *
     * @param sender user who executed the punishment or null for console
     * @param mode   mode name or null for global context
     * @param reason why the user was punished
     */
    public void mute(ICloudUser sender, String mode, String reason);

    /**
     * Mutes a user temporary.
     *
     * @param sender user who executed the punishment or null for console
     * @param mode   mode name or null for global context
     * @param length length of punishment in seconds
     * @param reason why the user was punished
     */
    public void tempMute(ICloudUser sender, String mode, long length, String reason);

    /**
     * Kicks a player.
     *
     * @param sender user who removed the punishment or null for console
     * @param mode   mode name or null for global context
     * @param reason why the user was punished
     */
    public void kick(ICloudUser sender, String mode, String reason);

    /**
     * Setts the locale of the user.
     *
     * @param locale {@link Locale#GERMAN} or {@link Locale#ENGLISH} are Supported
     * @param update boolean if the locale should be updated to database
     */
    public void setLocale(Locale locale, boolean update);

    /**
     * Returns the current locale of the user
     *
     * @return Locale
     */
    public Locale getLocale();


    /**
     * Returns current player, it can be either
     * {@link org.bukkit.entity.Player} or {@link net.md_5.bungee.api.connection.ProxiedPlayer}
     * Note: You cannot use this with offline load
     *
     * @param <T> valid Player type
     * @return valid Player
     */
    public <T> T getPlayer();
}
