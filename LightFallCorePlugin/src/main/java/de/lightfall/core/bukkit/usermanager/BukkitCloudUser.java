package de.lightfall.core.bukkit.usermanager;

import de.dytanic.cloudnet.driver.CloudNetDriver;
import de.dytanic.cloudnet.driver.service.ServiceInfoSnapshot;
import de.dytanic.cloudnet.driver.service.ServiceTask;
import de.dytanic.cloudnet.ext.bridge.BridgePlayerManager;
import de.dytanic.cloudnet.ext.bridge.player.ICloudPlayer;
import de.dytanic.cloudnet.ext.bridge.player.NetworkServiceInfo;
import de.lightfall.core.api.channelhandeler.ChannelHandler;
import de.lightfall.core.api.channelhandeler.documents.TeleportDocument;
import de.lightfall.core.api.usermanager.CloudUser;
import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.UUID;

public class BukkitCloudUser implements CloudUser {

    @Getter
    private final Player player;

    public BukkitCloudUser(Player player) {
        this.player = player;
    }

    @Override
    public void moveToPlayerTeleport(UUID uuid) {
        final NetworkServiceInfo networkServiceInfo = moveToPlayer(uuid);
        ServiceInfoSnapshot cloudService = CloudNetDriver.getInstance().getCloudServiceProvider().getCloudService(networkServiceInfo.getUniqueId());
        ChannelHandler.send(cloudService, new TeleportDocument(this.getUuid(), uuid));
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
        return BridgePlayerManager.getInstance().getOnlinePlayer(getUuid());
    }

    @Override
    public UUID getUuid() {
        return player.getUniqueId();
    }
}
