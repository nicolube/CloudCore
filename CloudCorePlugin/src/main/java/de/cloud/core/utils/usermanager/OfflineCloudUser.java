package de.cloud.core.utils.usermanager;

import co.aikar.commands.CommandManager;
import co.aikar.commands.MessageType;
import com.j256.ormlite.stmt.UpdateBuilder;
import de.cloud.core.InternalCoreAPI;
import de.cloud.core.api.Util;
import de.cloud.core.api.channelhandeler.ChannelHandler;
import de.cloud.core.api.channelhandeler.documents.MessageDocument;
import de.cloud.core.api.message.CoreMessageKeys;
import de.cloud.core.api.message.IMessageKeyProvider;
import de.cloud.core.api.punishments.IPunishment;
import de.cloud.core.api.punishments.PunishmentType;
import de.cloud.core.api.usermanager.ICloudUser;
import de.cloud.core.api.usermanager.IOfflineCloudUser;
import de.cloud.core.api.usermanager.IUserInfo;
import de.cloud.core.api.usermanager.IUserModeInfo;
import de.cloud.core.common.models.PunishmentModel;
import de.cloud.core.common.models.UserInfoModel;
import de.cloud.core.common.models.UserModeInfoModel;
import de.dytanic.cloudnet.common.concurrent.ITask;
import de.dytanic.cloudnet.driver.CloudNetDriver;
import de.dytanic.cloudnet.driver.service.ServiceInfoSnapshot;
import de.dytanic.cloudnet.ext.bridge.player.ICloudOfflinePlayer;
import de.dytanic.cloudnet.ext.bridge.player.ICloudPlayer;
import lombok.Getter;
import lombok.NonNull;
import lombok.SneakyThrows;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.query.QueryOptions;
import org.jetbrains.annotations.Nullable;

import java.sql.SQLException;
import java.util.*;
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
        UserInfoModel model = this.userManager.getPlugin().getDatabaseProvider().getUserInfoDao().queryForId(databaseId);
        return model.getId() == 0 ? null : model;
    }

    public CompletableFuture<IUserInfo> queryUserInfoAsync() {
        return CompletableFuture.supplyAsync(this::queryUserInfo);
    }

    @SneakyThrows
    public UserModeInfoModel queryUserModeInfo(String mode) {
        UserModeInfoModel model = this.userManager.getPlugin().getDatabaseProvider().getUserModeInfoDao().queryBuilder().where().eq("userInfo_id", this.databaseId).and().eq("mode", mode).queryForFirst();
        return model.getId() == 0 ? null : model;
    }

    public CompletableFuture<IUserModeInfo> queryUserModeInfoAsync(String mode) {
        return CompletableFuture.supplyAsync(() -> queryUserModeInfo(mode));
    }

    @SneakyThrows
    public List<? extends IPunishment> queryPunishments(String mode) {
        if (mode == null)
            return this.userManager.getPlugin().getDatabaseProvider().getPunishmentDao().queryBuilder().orderBy("created_at", false)
                    .where().eq("userInfo_id", this.databaseId).and().eq("userModeInfo", null).query();
        return this.userManager.getPlugin().getDatabaseProvider().getPunishmentDao().queryBuilder().orderBy("created_at", false)
                .where().eq("userInfo_id", this.databaseId).and().eq("userModeInfo", queryUserModeInfo(mode)).query();
    }

    public CompletableFuture<List<? extends IPunishment>> queryPunishmentsAsync(String mode) {
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
        punish(sender, mode, PunishmentType.TEMP_MUTE, length, reason);
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
                    updateBuilder = this.userManager.getPlugin().getDatabaseProvider().getUserInfoDao().updateBuilder();
                } else {
                    if (isBan) {
                        punishmentModel = queryUserModeInfo(mode).getActiveBan();
                    } else if (isMute) {
                        punishmentModel = queryUserModeInfo(mode).getActiveMute();
                    }
                    updateBuilder = this.userManager.getPlugin().getDatabaseProvider().getUserModeInfoDao().updateBuilder();
                }
                if (punishmentModel == null)
                    return false;
                String field;
                if (isBan) {
                    field = "activeBan_id";
                } else {
                    field = "activeMute_id";
                }
                punishmentModel.unPunish(sender.queryUserInfo(), new Date(), reason);
                this.userManager.getPlugin().getDatabaseProvider().getPunishmentDao().update(punishmentModel);
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
                punishmentModel = plugin.getDatabaseProvider().getPunishmentDao().createIfNotExists(punishmentModel);
                if (type.equals(PunishmentType.BAN) || type.equals(PunishmentType.TEMP_BAN)) {
                    final String formatBan = Util.formatBan(punishmentModel.getEnd(), reason, this.locale);
                    if (mode == null) {
                        UpdateBuilder<UserInfoModel, Long> updateBuilder = plugin.getDatabaseProvider().getUserInfoDao().updateBuilder().updateColumnValue("activeBan_id", punishmentModel.getId());
                        updateBuilder.where().idEq(this.databaseId);
                        updateBuilder.update();
                            kick(formatBan);
                    } else {
                        UpdateBuilder<UserModeInfoModel, Long> updateBuilder = plugin.getDatabaseProvider().getUserModeInfoDao().updateBuilder().updateColumnValue("activeBan_id", punishmentModel.getId());
                        updateBuilder.where().idEq(userModeInfoModel.getId()).and().eq("mode", mode);
                        updateBuilder.update();
                        // TODO Mode kick
                    }
                    return punishmentModel;
                }
                if (type.equals(PunishmentType.MUTE) || type.equals(PunishmentType.TEMP_MUTE)) {
                    if (mode == null) {
                        UpdateBuilder<UserInfoModel, Long> updateBuilder = plugin.getDatabaseProvider().getUserInfoDao().updateBuilder().updateColumnValue("activeMute_id", punishmentModel.getId());
                        updateBuilder.where().idEq(this.databaseId);
                        updateBuilder.update();
                    } else {
                        UpdateBuilder<UserModeInfoModel, Long> updateBuilder = plugin.getDatabaseProvider().getUserModeInfoDao().updateBuilder().updateColumnValue("activeMute_id", punishmentModel.getId());
                        updateBuilder.where().idEq(userModeInfoModel.getId()).and().eq("mode", mode);
                        updateBuilder.update();
                    }
                    sendMessage(MessageType.ERROR, CoreMessageKeys.MUTE, "{0}", punishmentModel.getReason(), "{1}",
                            Util.formatDate(punishmentModel.getEnd(), getLocale()));
                    return punishmentModel;
                }
                if (type.equals(PunishmentType.KICK)){
                    if (mode == null) {
                        CommandManager commandManager = this.userManager.getPlugin().getCommandManager();
                        //kick(CoreMessageKeys.KICK);
                    }
                    return punishmentModel;
                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            return null;
        });
    }
    private void kick(String message) {
        Objects.requireNonNull(getCloudPlayerAsync()).onComplete((iTask, iCloudPlayer) -> {
            if (iCloudPlayer == null) return;
            this.userManager.getPlayerManager().getPlayerExecutor(this.uuid).kick(message);
        });
    }

    private void modeKick(String message) {

    }

    @Override
    public void sendMessage(MessageType type, IMessageKeyProvider key, String... replacements) {
        this.userManager.getPlayerManager().getOnlinePlayerAsync(this.uuid).onComplete((iTask, iCloudPlayer) -> {
            if (iCloudPlayer == null) return;
            final ServiceInfoSnapshot cloudService = CloudNetDriver.getInstance().getCloudServiceProvider().getCloudService(iCloudPlayer.getConnectedService().getUniqueId());
            ChannelHandler.send(cloudService, new MessageDocument(this.uuid, type, key, replacements));
        });
    }

    @Override
    public CompletableFuture<Integer> getWight() {
        LuckPerms api = LuckPermsProvider.get();
        return api.getUserManager().loadUser(this.uuid).thenApply(user -> {
            QueryOptions queryOptions = api.getContextManager().getStaticQueryOptions();
            try {
                return Integer.parseInt(Objects.requireNonNull(user.getCachedData().getMetaData(queryOptions)
                        .getMetaValue("weight")));
            } catch (Exception ignore) {
                return 0;
            }
        });
    }

    public void setLocale(@NonNull String tag, boolean update) {
        setLocale(Locale.forLanguageTag(tag), update);
    }

    @Override
    public void setLocale(@NonNull Locale locale, boolean update) {
        this.locale = locale;
        if (!update) return;
        CompletableFuture.runAsync(() -> {
            try {
                final UpdateBuilder<UserInfoModel, Long> updateBuilder = this.userManager.getPlugin().getDatabaseProvider().getUserInfoDao()
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
    @Override
    public ICloudPlayer getCloudPlayer() {
        return this.userManager.getPlayerManager().getOnlinePlayer(this.uuid);
    }

    @Override
    public ITask<? extends ICloudPlayer> getCloudPlayerAsync() {
        return this.userManager.getPlayerManager().getOnlinePlayerAsync(this.uuid);
    }

    @Override
    public @Nullable ICloudOfflinePlayer getCloudOfflinePlayer() {
        return this.userManager.getPlayerManager().getOfflinePlayer(this.uuid);
    }

    @Override
    public ITask<ICloudOfflinePlayer> getCloudOfflinePlayerAsync() {
        return this.userManager.getPlayerManager().getOfflinePlayerAsync(this.uuid);
    }
}
