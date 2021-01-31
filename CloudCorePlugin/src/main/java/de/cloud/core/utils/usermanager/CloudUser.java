package de.cloud.core.utils.usermanager;

import co.aikar.commands.MessageType;
import de.cloud.core.api.Util;
import de.cloud.core.api.channelhandeler.ChannelHandler;
import de.cloud.core.api.channelhandeler.documents.MessageDocument;
import de.cloud.core.api.channelhandeler.documents.TeleportDocument;
import de.cloud.core.api.message.CoreMessageKeys;
import de.cloud.core.api.message.IMessageKeyProvider;
import de.cloud.core.api.punishments.PunishmentType;
import de.cloud.core.api.usermanager.ICloudUser;
import de.cloud.core.common.models.PunishmentModel;
import de.dytanic.cloudnet.common.concurrent.ITask;
import de.dytanic.cloudnet.driver.CloudNetDriver;
import de.dytanic.cloudnet.driver.service.ServiceInfoSnapshot;
import de.dytanic.cloudnet.driver.service.ServiceTask;
import de.dytanic.cloudnet.ext.bridge.player.ICloudPlayer;
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
        if (isOnline()) {
            this.userManager.getPlugin().getCommandManager().getCommandIssuer(getPlayer()).sendMessage(type, key, replacements);
            return;
        }
        super.sendMessage(type, key, replacements);
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
        final ITask<? extends ICloudPlayer> playerAsync = this.userManager.getPlayerManager().getOnlinePlayerAsync(uuid);
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
        this.userManager.getPlayerManager().getOnlinePlayerAsync(this.uuid).onComplete(p -> {
            if (p.getConnectedService().getServerName().equals(service)) return;
            this.userManager.getPlayerManager().getPlayerExecutor(this.uuid).connect(service);
        });
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