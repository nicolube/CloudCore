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
        this.locale = Locale.getDefault();
    }

    @SneakyThrows
    public UserInfoModel quarryUserInfo() {
        return this.userManager.getPlugin().getPlayerDao().queryForId(databaseId);
    }

    public CompletableFuture<IUserInfo> quarryUserInfoAsync() {
        return CompletableFuture.supplyAsync(() -> quarryUserInfo());
    }

    @SneakyThrows
    public UserModeInfoModel quarryUserModeInfo(String mode) {
        return this.userManager.getPlugin().getPlayerModeDao().queryBuilder().where().eq("playerInfo_id", this.databaseId).and().eq("mode", mode).queryForFirst();
    }

    public CompletableFuture<IUserModeInfo> quarryUserModeInfoAsync(String mode) {
        return CompletableFuture.supplyAsync(() -> quarryUserModeInfo(mode));
    }

    @SneakyThrows
    public List<? extends IPunishment> quarryPunishments(String mode) {
        if (mode == null)
            return this.userManager.getPlugin().getPunishmentDao().queryBuilder().orderBy("created_at", false)
                    .where().eq("userInfo_id", this.databaseId).and().eq("userModeInfo", null).query();
        return this.userManager.getPlugin().getPunishmentDao().queryBuilder().orderBy("created_at", false)
                .where().eq("userInfo_id", this.databaseId).and().eq("userModeInfo", quarryUserModeInfo(mode)).query();
    }

    public CompletableFuture<List<? extends IPunishment>> quarryPunishmentsAsync(String mode) {
        return CompletableFuture.supplyAsync(() -> quarryPunishments(mode));
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

    @SneakyThrows
    public CompletableFuture<Boolean> unBan(ICloudUser sender, String mode, String reason) {
        return CompletableFuture.supplyAsync(() -> {
            if (mode == null) {
                final PunishmentModel punishmentModel = quarryUserInfo().getActiveBan();
                if (punishmentModel == null)
                    return false;
                final UpdateBuilder<UserInfoModel, Long> updateBuilder = this.userManager.getPlugin().getPlayerDao().updateBuilder()
                        .updateColumnValue("activeBan", null);
                updateBuilder.where().idEq(this.databaseId);
                updateBuilder.update();
                return true;
            }
            final PunishmentModel punishmentModel = quarryUserModeInfo(mode).getActiveBan();
            if (punishmentModel == null)
                return false;
            final UpdateBuilder<UserInfoModel, Long> updateBuilder = this.userManager.getPlugin().getPlayerDao().updateBuilder()
                    .updateColumnValue("activeBan", null);
            updateBuilder.where().idEq(this.databaseId).and().eq("mode", mode);
            updateBuilder.update();
            return true;
        });
    }

    public void mute(ICloudUser sender, String mode, String reason) {
        punish(sender, mode, PunishmentType.MUTE, 0, reason);
    }

    public void tempMute(ICloudUser sender, String mode, long length, String reason) {
        punish(sender, mode, PunishmentType.TEMP_BAN, length, reason);
    }

    @SneakyThrows
    public CompletableFuture<Boolean> unMute(ICloudUser iSender, String mode, String reason) {
        CloudUser sender = (CloudUser) iSender;
        return CompletableFuture.supplyAsync(() -> {
            if (mode == null) {
                final PunishmentModel punishmentModel = quarryUserInfo().getActiveMute();
                if (punishmentModel == null)
                    return false;
                punishmentModel.unPunish(sender.quarryUserInfo(), new Date(), reason);
                this.userManager.getPlugin().getPunishmentDao().update(punishmentModel);
                final UpdateBuilder<UserInfoModel, Long> updateBuilder = this.userManager.getPlugin().getPlayerDao().updateBuilder()
                        .updateColumnValue("activeMute", null);
                updateBuilder.where().idEq(this.databaseId);
                updateBuilder.update();
                return true;
            }
            final PunishmentModel punishmentModel = quarryUserModeInfo(mode).getActiveMute();
            if (punishmentModel == null)
                return false;
            punishmentModel.unPunish(sender.quarryUserInfo(), new Date(), reason);
            final UpdateBuilder<UserInfoModel, Long> updateBuilder = this.userManager.getPlugin().getPlayerDao().updateBuilder()
                    .updateColumnValue("activeMute", null);
            updateBuilder.where().idEq(this.databaseId).and().eq("mode", mode);
            updateBuilder.update();
            return true;
        });
    }

    public void kick(ICloudUser sender, String mode, String reason) {
        punish(sender, mode, PunishmentType.KICK, 0, reason);
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
                userInfoModel = quarryUserInfo();
            else
                userModeInfoModel = quarryUserModeInfo(mode);
            PunishmentModel punishmentModel = new PunishmentModel(userInfoModel, null, sender.quarryUserInfo(),
                    type, endDate, reason);
            try {
                punishmentModel = plugin.getPunishmentDao().createIfNotExists(punishmentModel);
                if (type.equals(PunishmentType.BAN) || type.equals(PunishmentType.TEMP_BAN)) {
                    if (mode == null) {
                        UpdateBuilder<UserInfoModel, Long> updateBuilder = plugin.getPlayerDao().updateBuilder().updateColumnValue("activeBan", punishmentModel);
                        updateBuilder.where().idEq(userInfoModel.getId());
                        updateBuilder.update();
                        BridgePlayerManager.getInstance().proxyKickPlayer(getCloudPlayer(), Util.formatBan(endDate, reason, this.locale));
                    } else {
                        UpdateBuilder<UserModeInfoModel, Long> updateBuilder = plugin.getPlayerModeDao().updateBuilder().updateColumnValue("activeMute", punishmentModel);
                        updateBuilder.where().idEq(userModeInfoModel.getId()).and().eq("mode", mode);
                        updateBuilder.update();
                        // Todo send mode ban info to player (temporary solution)
                        BridgePlayerManager.getInstance().proxySendPlayerMessage(getCloudPlayer(), Util.formatBan(endDate, reason, this.locale));
                    }
                    return;
                }
                if (type.equals(PunishmentType.MUTE) || type.equals(PunishmentType.TEMP_MUTE)) {
                    if (mode == null) {
                        UpdateBuilder<UserInfoModel, Long> updateBuilder = plugin.getPlayerDao().updateBuilder().updateColumnValue("activeBan", punishmentModel);
                        updateBuilder.where().idEq(userInfoModel.getId());
                        updateBuilder.update();
                    } else {
                        UpdateBuilder<UserModeInfoModel, Long> updateBuilder = plugin.getPlayerModeDao().updateBuilder().updateColumnValue("activeBan", punishmentModel);
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

    public void setLocale(String tag) {
        setLocale(Locale.forLanguageTag(tag));
    }

    public void setLocale(Locale locale) {
        this.userManager.getPlugin().getCommandManager().setIssuerLocale(getPlayer(), locale);
        this.locale = locale;
    }

    public abstract <T> T getPlayer();

}