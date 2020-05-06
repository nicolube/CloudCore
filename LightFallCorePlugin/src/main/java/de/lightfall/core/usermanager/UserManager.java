package de.lightfall.core.usermanager;

import de.lightfall.core.InternalCoreAPI;
import de.lightfall.core.api.usermanager.IUserManager;
import de.lightfall.core.models.UserInfoModel;
import de.lightfall.core.models.UserModeInfoModel;
import lombok.SneakyThrows;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public abstract class UserManager implements IUserManager {

    @SneakyThrows
    public UserInfoModel quarryUserInfo(UUID uuid) {
        return this.getPlugin().getUserInfoDao().queryBuilder().where().eq("uuid", uuid).queryForFirst();
    }

    public CompletableFuture<UserInfoModel> quarryUserInfoAsync(UUID uuid) {
        return CompletableFuture.supplyAsync(() -> quarryUserInfo(uuid));
    }
    @SneakyThrows
    public UserModeInfoModel quarryUserModeInfo(UUID uuid, String mode) {
        return this.getPlugin().getUserModeInfoDao().queryBuilder().where().eq("uuid", uuid).and().eq("mode", mode).queryForFirst();
    }

    public CompletableFuture<UserModeInfoModel> quarryUserModeInfoAsync(UUID uuid, String mode) {
        return CompletableFuture.supplyAsync(() -> quarryUserModeInfo(uuid, mode));
    }

    public abstract InternalCoreAPI getPlugin();

    public OfflineCloudUser loadUser(UUID uuid) {
        final UserInfoModel userInfoModel = quarryUserInfo(uuid);
        return new OfflineCloudUser(uuid, null, userInfoModel.getId(), this);
    }
}
