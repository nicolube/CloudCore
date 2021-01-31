package de.cloud.core.api.usermanager;

import co.aikar.commands.MessageType;
import de.cloud.core.api.message.IMessageKeyProvider;
import de.cloud.core.api.punishments.IPunishment;
import de.dytanic.cloudnet.common.concurrent.ITask;
import de.dytanic.cloudnet.ext.bridge.player.ICloudOfflinePlayer;
import de.dytanic.cloudnet.ext.bridge.player.ICloudPlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Important: Do not cache any UserData!
 *
 */
public interface IOfflineCloudUser {

    /**
     * Return if the player is online in the current service
     *
     * @return boolean
     */
    boolean isOnline();

    /**
     * Gets the current {@link UUID}
     *
     * @return UUID of the current player
     */
    UUID getUuid();

    /**
     * Unbanns a user.
     *
     * @param sender user who removed the punishment or null for console
     * @param mode   mode name or null for global context
     * @param reason why the punishment get removed
     * @return CompletableFuture of Boolean (async) if the user was punished
     */
    CompletableFuture<Boolean> unBan(ICloudUser sender, String mode, String reason);

    /**
     * Unmute a user.
     *
     * @param sender user who removed the punishment or null for console
     * @param mode   mode name or null for global context
     * @param reason why the punishment get removed
     * @return CompletableFuture of Boolean (async) if the user was punished
     */
    CompletableFuture<Boolean> unMute(ICloudUser sender, String mode, String reason);

    /**
     * Quarry {@link IUserInfo} for current {@link ICloudUser}
     *
     * @return IUserInfo
     */
    IUserInfo queryUserInfo();

    /**
     * Quarry {@link IUserInfo} async for current {@link ICloudUser}
     *
     * @return CompletableFuture of IUserInfo
     */
    CompletableFuture<IUserInfo> queryUserInfoAsync();

    /**
     * Quarry {@link IUserModeInfo} for current {@link ICloudUser}
     *
     * @param mode who should by queried
     * @return IUserModeInfo
     */
    IUserModeInfo queryUserModeInfo(String mode);

    /**
     * Quarry {@link IUserModeInfo} async for current {@link ICloudUser}
     *
     * @param mode who should by queried
     * @return CompletableFuture of IUserModeInfo
     */
    CompletableFuture<IUserModeInfo> queryUserModeInfoAsync(String mode);

    /**
     * Quarry list of {@link IPunishment} for current {@link ICloudUser}
     * ordered by creation day
     *
     * @param mode who should by queried
     * @return CompletableFuture of IUserModeInfo
     */
    List<? extends IPunishment> queryPunishments(String mode);

    /**
     * Quarry list of {@link IPunishment} async for current {@link ICloudUser}
     * ordered by creation day
     *
     * @param mode who should by queried
     * @return CompletableFuture of List with IPunishment
     */
    CompletableFuture<List<? extends IPunishment>> queryPunishmentsAsync(String mode);


    /**
     * Banns a user permanent.
     *
     * @param sender user who executed the punishment or null for console
     * @param mode   mode name or null for global context
     * @param reason why the user was punished
     */
    void ban(ICloudUser sender, String mode, String reason);

    /**
     * Banns a user temporary.
     *
     * @param sender user who executed the punishment or null for console
     * @param mode   mode name or null for global context
     * @param length length of punishment in seconds
     * @param reason why the user was punished
     */
    void tempBan(ICloudUser sender, String mode, long length, String reason);

    /**
     * Mutes a user permanent.
     *
     * @param sender user who executed the punishment or null for console
     * @param mode   mode name or null for global context
     * @param reason why the user was punished
     */
    void mute(ICloudUser sender, String mode, String reason);

    /**
     * Mutes a user temporary.
     *
     * @param sender user who executed the punishment or null for console
     * @param mode   mode name or null for global context
     * @param length length of punishment in seconds
     * @param reason why the user was punished
     */
    void tempMute(ICloudUser sender, String mode, long length, String reason);

    /**
     * Kicks a player.
     *
     * @param sender user who removed the punishment or null for console
     * @param mode   mode name or null for global context
     * @param reason why the user was punished
     */
    void kick(ICloudUser sender, String mode, String reason);

    /**
     * Send a message to the player via a message kay you provided.
     *
     * @param type         {@link MessageType}
     * @param key          {@link IMessageKeyProvider}
     * @param replacements "key1", "value1", "key2", "value2"...
     */
    void sendMessage(MessageType type, IMessageKeyProvider key, String... replacements);

    /**
     * Gets group wight of user, is taken from luckperms
     *
     * @return CompletableFuture if Integer
     */
    CompletableFuture<Integer> getWight();

    /**
     * Setts the locale of the user.
     *
     * @param locale {@link Locale#GERMAN} or {@link Locale#ENGLISH} are Supported
     * @param update boolean if the locale should be updated to database
     */
    void setLocale(Locale locale, boolean update);

    /**
     * Returns the current locale of the user
     *
     * @return Locale
     */
    Locale getLocale();

    /**
     * Gets the current {@link ICloudPlayer}, returns null when offline or not exist
     *
     * @return ICloudPlayer of the current player
     */
    @Nullable ICloudPlayer getCloudPlayer();

    /**
     * Gets the current {@link ICloudPlayer}, returns null when offline or net exist
     *
     * @return ITask of ICloudPlayer from the current player
     */
    @Nullable ITask<? extends ICloudPlayer> getCloudPlayerAsync();

    /**
     * Gets the corresponding offline player, returns null when not exist
     *
     * @return ICloudOfflinePlayer from the current player
     */
    @Nullable ICloudOfflinePlayer getCloudOfflinePlayer();

    /**
     * Gets the corresponding offline player, returns null when not exist
     *
     * @return ITask of ICloudOfflinePlayer from the current player
     */
    ITask<ICloudOfflinePlayer> getCloudOfflinePlayerAsync();

}
