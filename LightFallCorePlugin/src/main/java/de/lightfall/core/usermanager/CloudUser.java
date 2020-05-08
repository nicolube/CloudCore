package de.lightfall.core.usermanager;

import co.aikar.commands.MessageType;
import de.dytanic.cloudnet.common.concurrent.ITask;
import de.dytanic.cloudnet.driver.CloudNetDriver;
import de.dytanic.cloudnet.driver.service.ServiceInfoSnapshot;
import de.dytanic.cloudnet.driver.service.ServiceTask;
import de.dytanic.cloudnet.ext.bridge.BridgePlayerManager;
import de.dytanic.cloudnet.ext.bridge.player.ICloudPlayer;
import de.lightfall.core.api.Util;
import de.lightfall.core.api.channelhandeler.ChannelHandler;
import de.lightfall.core.api.channelhandeler.documents.MessageDocument;
import de.lightfall.core.api.channelhandeler.documents.TeleportDocument;
import de.lightfall.core.api.message.IMessageKeyProvider;
import de.lightfall.core.api.punishments.PunishmentType;
import de.lightfall.core.api.usermanager.ICloudUser;
import de.lightfall.core.models.PunishmentModel;
import lombok.NonNull;
import net.md_5.bungee.api.ChatColor;

import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public abstract class CloudUser extends OfflineCloudUser implements ICloudUser {


    public CloudUser(UUID uuid, String realName, long databaseId, UserManager userManager) {
        super(uuid, realName, databaseId, userManager);
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
        return BridgePlayerManager.getInstance().getOnlinePlayer(this.uuid);
    }

    @Override
    public CompletableFuture<PunishmentModel> punish(ICloudUser iSender, String mode, PunishmentType type, long length, String reason) {
        final CompletableFuture<PunishmentModel> punish = super.punish(iSender, mode, type, length, reason);
        punish.thenAcceptAsync(punishmentModel -> {
            final boolean isBan = type.equals(PunishmentType.BAN) || type.equals(PunishmentType.TEMP_BAN);
            final boolean isMute = type.equals(PunishmentType.MUTE) || type.equals(PunishmentType.TEMP_MUTE);
            final ICloudPlayer cloudPlayer = getCloudPlayer();
            if (isBan) {
                final String formatBan = Util.formatBan(punishmentModel.getEnd(), reason, this.locale);
                if (mode == null) {
                    BridgePlayerManager.getInstance().proxyKickPlayer(getCloudPlayer(), formatBan);
                    return;
                }
                move("Lobby-1");
                // Todo send mode ban info to player (temporary solution)
                BridgePlayerManager.getInstance().proxySendPlayerMessage(cloudPlayer, formatBan);
                return;
            }
            if (isMute) {
                // Todo send mute info to player
                return;
            }
            if (type.equals(PunishmentType.KICK)) {
                if (mode == null)
                    BridgePlayerManager.getInstance().proxyKickPlayer(getCloudPlayer(), ChatColor.translateAlternateColorCodes('&', reason));
                // Todo send kick info to player
                move("Lobby-1");
            }
        });

        return punish;
    }

    @Override
    public void setLocale(@NonNull Locale locale, boolean update) {
        this.userManager.getPlugin().getCommandManager().setIssuerLocale(getPlayer(), locale);
        super.setLocale(locale, update);
    }

    public abstract <T> T getPlayer();

    @Override
    public abstract boolean isOnline();
}