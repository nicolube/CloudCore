package de.lightfall.core.usermanager;

import co.aikar.commands.MessageType;
import com.j256.ormlite.stmt.UpdateBuilder;
import de.dytanic.cloudnet.common.concurrent.ITask;
import de.dytanic.cloudnet.driver.CloudNetDriver;
import de.dytanic.cloudnet.driver.service.ServiceInfoSnapshot;
import de.dytanic.cloudnet.driver.service.ServiceTask;
import de.dytanic.cloudnet.ext.bridge.BridgePlayerManager;
import de.dytanic.cloudnet.ext.bridge.player.ICloudPlayer;
import de.lightfall.core.InternalCoreAPI;
import de.lightfall.core.api.Util;
import de.lightfall.core.api.channelhandeler.ChannelHandler;
import de.lightfall.core.api.channelhandeler.documents.MessageDocument;
import de.lightfall.core.api.channelhandeler.documents.TeleportDocument;
import de.lightfall.core.api.message.IMessageKeyProvider;
import de.lightfall.core.api.punishments.IPunishment;
import de.lightfall.core.api.punishments.IUserInfo;
import de.lightfall.core.api.punishments.IUserModeInfo;
import de.lightfall.core.api.punishments.PunishmentType;
import de.lightfall.core.api.usermanager.ICloudUser;
import de.lightfall.core.models.PunishmentModel;
import de.lightfall.core.models.UserInfoModel;
import de.lightfall.core.models.UserModeInfoModel;
import lombok.Getter;
import lombok.NonNull;
import lombok.SneakyThrows;
import net.md_5.bungee.api.ChatColor;

import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public abstract class CloudUser implements ICloudUser {


    @Getter
    private final UUID uuid;
    @Getter
    protected String realName;
    @Getter
    private final long databaseId;
    protected final UserManager userManager;
    @Getter
    private Locale locale;

    public CloudUser(UUID uuid, String realName, long databaseId, UserManager userManager) {
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

    @Override
    public void sendMessage(MessageType type, IMessageKeyProvider key, String... replacements) {
        BridgePlayerManager.getInstance().getOnlinePlayerAsync(this.uuid).onComplete((iTask, iCloudPlayer) -> {
            final ServiceInfoSnapshot cloudService = CloudNetDriver.getInstance().getCloudServiceProvider().getCloudService(iCloudPlayer.getConnectedService().getUniqueId());
            ChannelHandler.send(cloudService, new MessageDocument(this.uuid, type, key, replacements));
        });
    }

    @Override
    public void moveToPlayerTeleport(UUID uuid) {
        moveToPlayer(uuid).onComplete(player -> {
            ServiceInfoSnapshot cloudService = CloudNetDriver.getInstance().getCloudServiceProvider().getCloudService(player.getConnectedService().getUniqueId());
            ChannelHandler.send(cloudService, new TeleportDocument(this.uuid, uuid));
        });
    }

    @Override
    public ITask<? extends ICloudPlayer> moveToPlayer(UUID uuid) {
        final ITask<? extends ICloudPlayer> playerAsync = BridgePlayerManager.getInstance().getOnlinePlayerAsync(uuid);
        playerAsync.onComplete(p -> move(p.getConnectedService().getServerName()));
        return playerAsync;
    }

    @Override
    public void move(UUID service) {
        CloudNetDriver.getInstance().getCloudServiceProvider().getCloudServiceAsync(service).onComplete(s -> move(s.getServiceId().getName()));
    }

    @Override
    public void move(ServiceTask service) {
        move(service.getName());
    }

    @Override
    public void move(ServiceInfoSnapshot service) {
        move(service.getServiceId().getName());
    }

    @Override
    public void move(String service) {
        BridgePlayerManager.getInstance().getOnlinePlayerAsync(getUuid()).onComplete(p -> {
            if (p.getConnectedService().getServerName().equals(service)) return;
            BridgePlayerManager.getInstance().proxySendPlayer(getCloudPlayer(), service);
        });
    }

    @Override
    public ICloudPlayer getCloudPlayer() {
        return BridgePlayerManager.getInstance().getOnlinePlayer(uuid);
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

    public void punish(ICloudUser iSender, String mode, PunishmentType type, long length, String reason) {
        CloudUser sender = (CloudUser) iSender;
        CompletableFuture.runAsync(() -> {
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
                        BridgePlayerManager.getInstance().proxyKickPlayer(getCloudPlayer(), Util.formatBan(endDate, reason, this.locale));
                    } else {
                        UpdateBuilder<UserModeInfoModel, Long> updateBuilder = plugin.getUserModeInfoDao().updateBuilder().updateColumnValue("activeBan_id", punishmentModel.getId());
                        updateBuilder.where().idEq(userModeInfoModel.getId()).and().eq("mode", mode);
                        updateBuilder.update();
                        // Todo send mode ban info to player (temporary solution)
                        BridgePlayerManager.getInstance().proxySendPlayerMessage(getCloudPlayer(), Util.formatBan(endDate, reason, this.locale));
                    }
                    return;
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
                    // Todo send mute info to player
                    return;
                }
                if (type.equals(PunishmentType.KICK)) {
                    if (mode == null)
                        BridgePlayerManager.getInstance().proxyKickPlayer(getCloudPlayer(), ChatColor.translateAlternateColorCodes('&', reason));
                    // Todo send kick info to player
                    move("Lobby-1");
                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        });
    }

    public void setLocale(@NonNull String tag, boolean update) {
        setLocale(Locale.forLanguageTag(tag), update);
    }

    public void setLocale(@NonNull Locale locale, boolean update) {
        this.userManager.getPlugin().getCommandManager().setIssuerLocale(getPlayer(), locale);
        this.locale = locale;
        if (!update) return;
        CompletableFuture.runAsync(() -> {
            try {
                final UpdateBuilder<UserInfoModel, Long> updateBuilder = this.userManager.getPlugin().getUserInfoDao()
                        .updateBuilder().updateColumnValue("locale", locale.getLanguage());
                updateBuilder.where().idEq(this.databaseId);
                updateBuilder.update();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        });
    }

    public abstract <T> T getPlayer();
}