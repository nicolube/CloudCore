package de.lightfall.core.bungee.usermanager;

import de.dytanic.cloudnet.driver.CloudNetDriver;
import de.dytanic.cloudnet.driver.service.ServiceInfoSnapshot;
import de.dytanic.cloudnet.driver.service.ServiceTask;
import de.dytanic.cloudnet.ext.bridge.BridgePlayerManager;
import de.dytanic.cloudnet.ext.bridge.player.ICloudPlayer;
import de.dytanic.cloudnet.ext.bridge.player.NetworkServiceInfo;
import de.lightfall.core.api.channelhandeler.ChannelHandler;
import de.lightfall.core.api.channelhandeler.documents.TeleportDocument;
import de.lightfall.core.api.usermanager.CloudUser;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.UUID;

public class BungeeCloudUser implements CloudUser {

    @Getter
    private final UUID uuid;
    @Getter @Setter(value = AccessLevel.PROTECTED)
    private ProxiedPlayer player;

    public BungeeCloudUser(UUID uuid) {
        this.uuid = uuid;
    }

    @Override
    public void moveToPlayerTeleport(UUID uuid) {
        final NetworkServiceInfo networkServiceInfo = moveToPlayer(uuid);
        ServiceInfoSnapshot cloudService = CloudNetDriver.getInstance().getCloudServiceProvider().getCloudService(networkServiceInfo.getUniqueId());
        ChannelHandler.send(cloudService, new TeleportDocument(this.uuid, uuid));
    }

    @Override
    public NetworkServiceInfo moveToPlayer(UUID uuid) {
        final NetworkServiceInfo targetService = BridgePlayerManager.getInstance().getOnlinePlayer(uuid).getConnectedService();
        move(targetService.getServerName());
        return targetService;
    }

    @Override
    public void move(UUID service) {
        move(CloudNetDriver.getInstance().getCloudServiceProvider().getCloudService(service).getServiceId().getName());
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
        BridgePlayerManager.getInstance().proxySendPlayer(getCloudPlayer(), service);
    }

    @Override
    public ICloudPlayer getCloudPlayer() {
        return BridgePlayerManager.getInstance().getOnlinePlayer(uuid);
    }
}
