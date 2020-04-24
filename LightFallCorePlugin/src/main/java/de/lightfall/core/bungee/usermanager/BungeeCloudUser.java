package de.lightfall.core.bungee.usermanager;

import de.dytanic.cloudnet.common.document.gson.JsonDocument;
import de.dytanic.cloudnet.driver.CloudNetDriver;
import de.dytanic.cloudnet.driver.service.ServiceInfoSnapshot;
import de.dytanic.cloudnet.driver.service.ServiceTask;
import de.dytanic.cloudnet.ext.bridge.BridgePlayerManager;
import de.dytanic.cloudnet.ext.bridge.player.ICloudPlayer;
import de.dytanic.cloudnet.ext.bridge.player.NetworkServiceInfo;
import de.lightfall.core.api.MessageChannels;
import de.lightfall.core.api.usermanager.CloudUser;
import lombok.Getter;

import java.util.UUID;

public class BungeeCloudUser implements CloudUser {

    @Getter
    private UUID uuid;


    @Override
    public void moveToPlayerTeleport(UUID uuid) {
        final NetworkServiceInfo networkServiceInfo = moveToPlayer(uuid);
        ServiceInfoSnapshot cloudService = CloudNetDriver.getInstance().getCloudServiceProvider().getCloudService(networkServiceInfo.getUniqueId());
        final JsonDocument document = new JsonDocument();
        document.append("uuid", uuid.toString());
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
