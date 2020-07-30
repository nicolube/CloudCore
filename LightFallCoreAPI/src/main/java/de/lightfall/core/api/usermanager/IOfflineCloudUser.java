package de.lightfall.core.api.usermanager;

import de.lightfall.core.api.punishments.IPunishment;

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
}
