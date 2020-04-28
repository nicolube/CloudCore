package de.lightfall.core.bukkit.usermanager;

import de.dytanic.cloudnet.common.concurrent.ITask;
import de.dytanic.cloudnet.driver.CloudNetDriver;
import de.dytanic.cloudnet.driver.service.ServiceInfoSnapshot;
import de.dytanic.cloudnet.driver.service.ServiceTask;
import de.dytanic.cloudnet.ext.bridge.BridgePlayerManager;
import de.dytanic.cloudnet.ext.bridge.player.ICloudPlayer;
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
        moveToPlayer(uuid).onComplete(player -> {
            ServiceInfoSnapshot cloudService = CloudNetDriver.getInstance().getCloudServiceProvider().getCloudService(player.getConnectedService().getUniqueId());
            ChannelHandler.send(cloudService, new TeleportDocument(this.getUuid(), uuid));
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
        BridgePlayerManager.getInstance().getOnlinePlayerAsync(getUuid()).onComplete(p ->{
            if (p.getConnectedService().getServerName().equals(service)) return;
            BridgePlayerManager.getInstance().proxySendPlayer(getCloudPlayer(), service);
        });
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
