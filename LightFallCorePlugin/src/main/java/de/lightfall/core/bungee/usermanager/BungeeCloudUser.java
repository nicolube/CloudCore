package de.lightfall.core.bungee.usermanager;

import co.aikar.commands.MessageType;
import de.dytanic.cloudnet.common.concurrent.ITask;
import de.dytanic.cloudnet.driver.CloudNetDriver;
import de.dytanic.cloudnet.driver.service.ServiceInfoSnapshot;
import de.dytanic.cloudnet.driver.service.ServiceTask;
import de.dytanic.cloudnet.ext.bridge.BridgePlayerManager;
import de.dytanic.cloudnet.ext.bridge.player.ICloudPlayer;
import de.lightfall.core.api.IMessageKeyProvider;
import de.lightfall.core.api.channelhandeler.ChannelHandler;
import de.lightfall.core.api.channelhandeler.documents.MessageDocument;
import de.lightfall.core.api.channelhandeler.documents.TeleportDocument;
import de.lightfall.core.api.usermanager.ICloudUser;
import de.lightfall.core.bungee.MainBungee;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.UUID;

public class BungeeCloudUser implements ICloudUser {

    @Getter
    private final UUID uuid;
    @Getter @Setter(value = AccessLevel.PROTECTED)
    private ProxiedPlayer player;

    public BungeeCloudUser(UUID uuid) {
        this.uuid = uuid;
    }

    @Override
    public void moveToPlayerTeleport(UUID uuid) {
        moveToPlayer(uuid).onComplete(player -> {
            ServiceInfoSnapshot cloudService = CloudNetDriver.getInstance().getCloudServiceProvider().getCloudService(player.getConnectedService().getUniqueId());
            ChannelHandler.send(cloudService, new TeleportDocument(this.uuid, uuid));
        });
    }

    @Override
    public void sendMessage(MessageType type, IMessageKeyProvider key, String... replacements) {
        if (isOnline()) {
            MainBungee.getInstance().getCommandManager().getCommandIssuer(player).sendMessage(type, key, replacements);
            return;
        }
        ChannelHandler.send(new MessageDocument(this.uuid, type, key, replacements));
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
        BridgePlayerManager.getInstance().getOnlinePlayerAsync(getUuid()).onComplete(p ->{
            if (p.getConnectedService().getServerName().equals(service)) return;
            BridgePlayerManager.getInstance().proxySendPlayer(getCloudPlayer(), service);
        });
    }

    @Override
    public ICloudPlayer getCloudPlayer() {
        return BridgePlayerManager.getInstance().getOnlinePlayer(uuid);
    }

    @Override
    public boolean isOnline() {
        return this.player != null && this.player.isConnected();
    }
}
