package de.lightfall.core.usermanager;

import com.j256.ormlite.stmt.UpdateBuilder;
import de.lightfall.core.InternalCoreAPI;
import de.lightfall.core.api.punishments.IPunishment;
import de.lightfall.core.api.punishments.IUserInfo;
import de.lightfall.core.api.punishments.IUserModeInfo;
import de.lightfall.core.api.punishments.PunishmentType;
import de.lightfall.core.api.usermanager.ICloudUser;
import de.lightfall.core.api.usermanager.IOfflineCloudUser;
import de.lightfall.core.models.PunishmentModel;
import de.lightfall.core.models.UserInfoModel;
import de.lightfall.core.models.UserModeInfoModel;
import lombok.Getter;
import lombok.NonNull;
import lombok.SneakyThrows;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class OfflineCloudUser implements IOfflineCloudUser {

    @Getter
    protected final UUID uuid;
    @Getter
    protected String realName;
    @Getter
    protected final long databaseId;
    protected final UserManager userManager;
    @Getter
    protected Locale locale;

    public OfflineCloudUser(UUID uuid, String realName, long databaseId, UserManager userManager) {
        this.uuid = uuid;
        this.realName = realName;
        this.databaseId = databaseId;
        this.userManager = userManager;
    }

    @SneakyThrows
    public UserInfoModel queryUserInfo() {
        return this.userManager.getPlugin().getUserInfoDao().queryForId(databaseId);
    }

    public CompletableFuture<IUserInfo> quarryUserInfoAsync() {
        return CompletableFuture.supplyAsync(() -> queryUserInfo());
    }

    @SneakyThrows
    public UserModeInfoModel queryUserModeInfo(String mode) {
        return this.userManager.getPlugin().getUserModeInfoDao().queryBuilder().where().eq("playerInfo_id", this.databaseId).and().eq("mode", mode).queryForFirst();
    }

    public CompletableFuture<IUserModeInfo> queryUserModeInfoAsync(String mode) {
        return CompletableFuture.supplyAsync(() -> queryUserModeInfo(mode));
    }

    @SneakyThrows
    public List<? extends IPunishment> queryPunishments(String mode) {
        if (mode == null)
            return this.userManager.getPlugin().getPunishmentDao().queryBuilder().orderBy("created_at", false)
                    .where().eq("userInfo_id", this.databaseId).and().eq("userModeInfo", null).query();
        return this.userManager.getPlugin().getPunishmentDao().queryBuilder().orderBy("created_at", false)
                .where().eq("userInfo_id", this.databaseId).and().eq("userModeInfo", queryUserModeInfo(mode)).query();
    }

    public CompletableFuture<List<? extends IPunishment>> quarryPunishmentsAsync(String mode) {
        return CompletableFuture.supplyAsync(() -> queryPunishments(mode));
    }

    public void ban(ICloudUser sender, String mode, String reason) {
        punish(sender, mode, PunishmentType.BAN, 0, reason);
    }

    public void tempBan(ICloudUser sender, String mode, long length, String reason) {
        punish(sender, mode, PunishmentType.TEMP_BAN, length, reason);
    }

    public CompletableFuture<Boolean> unBan(ICloudUser sender, String mode, String reason) {
        return unPunish(sender, PunishmentType.BAN, mode, reason);
    }

    public void mute(ICloudUser sender, String mode, String reason) {
        punish(sender, mode, PunishmentType.MUTE, 0, reason);
    }

    public void tempMute(ICloudUser sender, String mode, long length, String reason) {
        punish(sender, mode, PunishmentType.TEMP_BAN, length, reason);
    }

    public CompletableFuture<Boolean> unMute(ICloudUser sender, String mode, String reason) {
        return unPunish(sender, PunishmentType.MUTE, mode, reason);
    }

    public void kick(ICloudUser sender, String mode, String reason) {
        punish(sender, mode, PunishmentType.KICK, 0, reason);
    }


    public CompletableFuture<Boolean> unPunish(ICloudUser iSender, PunishmentType type, String mode, String reason) {
        CloudUser sender = (CloudUser) iSender;
        return CompletableFuture.supplyAsync(() -> {
            try {
                PunishmentModel punishmentModel = null;
                final UpdateBuilder<?, Long> updateBuilder;
                final boolean isBan = type.equals(PunishmentType.BAN) || type.equals(PunishmentType.TEMP_BAN);
                final boolean isMute = type.equals(PunishmentType.MUTE) || type.equals(PunishmentType.TEMP_MUTE);
                if (mode == null) {
                    if (isBan) {
                        punishmentModel = queryUserInfo().getActiveBan();
                    } else if (isMute) {
                        punishmentModel = queryUserInfo().getActiveMute();
                    }
                    updateBuilder = this.userManager.getPlugin().getUserInfoDao().updateBuilder();
                } else {
                    if (isBan) {
                        punishmentModel = queryUserModeInfo(mode).getActiveBan();
                    } else if (isMute) {
                        punishmentModel = queryUserModeInfo(mode).getActiveMute();
                    }
                    updateBuilder = this.userManager.getPlugin().getUserModeInfoDao().updateBuilder();
                }
                if (punishmentModel == null)
                    return false;
                String field = null;
                if (isBan) {
                    field = "activeBan_id";
                } else if (isMute) {
                    field = "activeMute_id";
                }
                punishmentModel.unPunish(sender.queryUserInfo(), new Date(), reason);
                this.userManager.getPlugin().getPunishmentDao().update(punishmentModel);
                updateBuilder.updateColumnValue(field, null);
                updateBuilder.where().idEq(this.databaseId);
                updateBuilder.update();
                return true;
            } catch (SQLException ex) {
                ex.printStackTrace();
                return false;
            }
        });
    }

    public CompletableFuture<PunishmentModel> punish(ICloudUser iSender, String mode, PunishmentType type, long length, String reason) {
        CloudUser sender = (CloudUser) iSender;
        return CompletableFuture.supplyAsync(() -> {
            final InternalCoreAPI plugin = this.userManager.getPlugin();
            Date endDate = null;
            if (length > 0) {
                endDate = Date.from(new Date().toInstant().plusSeconds(length));
            }
            UserInfoModel userInfoModel = null;
            UserModeInfoModel userModeInfoModel = null;
            if (mode == null)
                userInfoModel = queryUserInfo();
            else
                userModeInfoModel = queryUserModeInfo(mode);
            PunishmentModel punishmentModel = new PunishmentModel(userInfoModel, null, sender.queryUserInfo(),
                    type, endDate, reason);
            try {
                punishmentModel = plugin.getPunishmentDao().createIfNotExists(punishmentModel);
                if (type.equals(PunishmentType.BAN) || type.equals(PunishmentType.TEMP_BAN)) {
                    if (mode == null) {
                        UpdateBuilder<UserInfoModel, Long> updateBuilder = plugin.getUserInfoDao().updateBuilder().updateColumnValue("activeBan_id", punishmentModel.getId());
                        updateBuilder.where().idEq(this.databaseId);
                        updateBuilder.update();
                    } else {
                        UpdateBuilder<UserModeInfoModel, Long> updateBuilder = plugin.getUserModeInfoDao().updateBuilder().updateColumnValue("activeBan_id", punishmentModel.getId());
                        updateBuilder.where().idEq(userModeInfoModel.getId()).and().eq("mode", mode);
                        updateBuilder.update();
                    }
                    return punishmentModel;
                }
                if (type.equals(PunishmentType.MUTE) || type.equals(PunishmentType.TEMP_MUTE)) {
                    if (mode == null) {
                        UpdateBuilder<UserInfoModel, Long> updateBuilder = plugin.getUserInfoDao().updateBuilder().updateColumnValue("activeMute_id", punishmentModel.getId());
                        updateBuilder.where().idEq(this.databaseId);
                        updateBuilder.update();
                    } else {
                        UpdateBuilder<UserModeInfoModel, Long> updateBuilder = plugin.getUserModeInfoDao().updateBuilder().updateColumnValue("activeMute_id", punishmentModel.getId());
                        updateBuilder.where().idEq(userModeInfoModel.getId()).and().eq("mode", mode);
                        updateBuilder.update();
                    }
                    return punishmentModel;
                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            return null;
        });
    }

    public void setLocale(@NonNull String tag, boolean update) {
        setLocale(Locale.forLanguageTag(tag), update);
    }

    public void setLocale(@NonNull Locale locale, boolean update) {
        this.locale = locale;
        if (!update) return;
        CompletableFuture.runAsync(() -> {
            try {
                final UpdateBuilder<UserInfoModel, Long> updateBuilder = this.userManager.getPlugin().getUserInfoDao()
                        .updateBuilder().updateColumnValue("locale", locale.getLanguage());
                updateBuilder.where().idEq(this.getDatabaseId());
                updateBuilder.update();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        });
    }

    @Override
    public boolean isOnline() {
        return false;
    }
}
