package de.cloud.core.utils.usermanager;

import de.cloud.core.InternalCoreAPI;
import de.cloud.core.api.usermanager.IUserManager;
import de.cloud.core.common.models.UserInfoModel;
import de.cloud.core.common.models.UserModeInfoModel;
import de.dytanic.cloudnet.driver.CloudNetDriver;
import de.dytanic.cloudnet.ext.bridge.player.IPlayerManager;
import lombok.Getter;
import lombok.SneakyThrows;

import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public abstract class UserManager implements IUserManager {

    @Getter
    private final IPlayerManager playerManager = CloudNetDriver.getInstance().getServicesRegistry().getFirstService(IPlayerManager.class);
    private final InternalCoreAPI plugin;

    public UserManager(InternalCoreAPI plugin) {
        this.plugin = plugin;
        this.plugin.getCommandManager().onLocaleChange((issuer, oldLocale, newLocale) -> {
            CloudUser user = getUser(issuer.getUniqueId());
            Locale locale = user.getLocale();
            if (newLocale != locale) user.setLocale(locale, false);
        });
    }

    @SneakyThrows
    public UserInfoModel quarryUserInfo(UUID uuid) {
        return this.getPlugin().getDatabaseProvider().getUserInfoDao().queryBuilder().where().eq("uuid", uuid).queryForFirst();
    }

    public CompletableFuture<UserInfoModel> quarryUserInfoAsync(UUID uuid) {
        return CompletableFuture.supplyAsync(() -> quarryUserInfo(uuid));
    }

    @SneakyThrows
    public UserModeInfoModel quarryUserModeInfo(long userDatabaseId, String mode) {
        if (mode == null) return null;
        return this.getPlugin().getDatabaseProvider().getUserModeInfoDao().queryBuilder().where().eq("userInfo_id", userDatabaseId).and().eq("mode", mode).queryForFirst();
    }

    public CompletableFuture<UserModeInfoModel> quarryUserModeInfoAsync(long userDatabaseId, String mode) {
        return CompletableFuture.supplyAsync(() -> quarryUserModeInfo(userDatabaseId, mode));
    }

    public abstract InternalCoreAPI getPlugin();

    public abstract CloudUser getUser(UUID uuid);

    public CompletableFuture<? extends OfflineCloudUser> loadUser(UUID uuid) {
        return CompletableFuture.supplyAsync(() -> {
            final CloudUser user = getUser(uuid);
            if (user != null) return user;
            final UserInfoModel userInfoModel = quarryUserInfo(uuid);
            if (userInfoModel == null) return null;
            return new OfflineCloudUser(uuid, null, userInfoModel.getId(), this);
        });
    }
}
