package de.lightfall.core.api.usermanager;

import de.lightfall.core.api.punishments.IPunishment;
import de.lightfall.core.api.punishments.IUserInfo;
import de.lightfall.core.api.punishments.IUserModeInfo;

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
    public boolean isOnline();

    /**
     * Gets the current {@link UUID}
     *
     * @return UUID of the current player
     */
    public UUID getUuid();

    /**
     * Unbanns a user.
     *
     * @param sender user who removed the punishment or null for console
     * @param mode   mode name or null for global context
     * @param reason why the punishment get removed
     * @return CompletableFuture<Boolean> (async) if the user was punished
     */
    public CompletableFuture<Boolean> unBan(ICloudUser sender, String mode, String reason);

    /**
     * Unmute a user.
     *
     * @param sender user who removed the punishment or null for console
     * @param mode   mode name or null for global context
     * @param reason why the punishment get removed
     * @return CompletableFuture<Boolean> (async) if the user was punished
     */
    public CompletableFuture<Boolean> unMute(ICloudUser sender, String mode, String reason);

    /**
     * Quarry {@link IUserInfo} for current {@link ICloudUser}
     *
     * @return IUserInfo
     */
    public IUserInfo queryUserInfo();

    /**
     * Quarry {@link IUserInfo} async for current {@link ICloudUser}
     *
     * @return CompletableFuture<IUserInfo>
     */
    public CompletableFuture<IUserInfo> quarryUserInfoAsync();

    /**
     * Quarry {@link IUserModeInfo} for current {@link ICloudUser}
     *
     * @return IUserModeInfo
     */
    public IUserModeInfo queryUserModeInfo(String mode);

    /**
     * Quarry {@link IUserModeInfo} async for current {@link ICloudUser}
     *
     * @return CompletableFuture<IUserModeInfo>
     */
    public CompletableFuture<IUserModeInfo> queryUserModeInfoAsync(String mode);

    /**
     * Quarry list of {@link IPunishment} for current {@link ICloudUser}
     * ordered by creation day
     *
     * @return CompletableFuture<IUserModeInfo>
     */
    public List<? extends IPunishment> queryPunishments(String mode);

    /**
     * Quarry list of {@link IPunishment} async for current {@link ICloudUser}
     * ordered by creation day
     *
     * @return CompletableFuture<List < ? extends IPunishment>>
     */
    public CompletableFuture<List<? extends IPunishment>> quarryPunishmentsAsync(String mode);


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
}
