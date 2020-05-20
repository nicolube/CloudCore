package de.lightfall.core.usermanager;

import de.dytanic.cloudnet.driver.CloudNetDriver;
import de.dytanic.cloudnet.ext.bridge.player.IPlayerManager;
import de.lightfall.core.InternalCoreAPI;
import de.lightfall.core.api.usermanager.IUserManager;
import de.lightfall.core.models.UserInfoModel;
import de.lightfall.core.models.UserModeInfoModel;
import lombok.Getter;
import lombok.SneakyThrows;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public abstract class UserManager implements IUserManager {

    @Getter
    private final IPlayerManager playerManager = CloudNetDriver.getInstance().getServicesRegistry().getFirstService(IPlayerManager.class);


    @SneakyThrows
    public UserInfoModel quarryUserInfo(UUID uuid) {
        return this.getPlugin().getUserInfoDao().queryBuilder().where().eq("uuid", uuid).queryForFirst();
    }

    public CompletableFuture<UserInfoModel> quarryUserInfoAsync(UUID uuid) {
        return CompletableFuture.supplyAsync(() -> quarryUserInfo(uuid));
    }

    @SneakyThrows
    public UserModeInfoModel quarryUserModeInfo(long userDatabaseId, String mode) {
        if (mode == null) return null;
        return this.getPlugin().getUserModeInfoDao().queryBuilder().where().eq("userInfo_id", userDatabaseId).and().eq("mode", mode).queryForFirst();
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
