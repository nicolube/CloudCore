package de.lightfall.core.api.usermanager;

import de.dytanic.cloudnet.driver.service.ServiceInfoSnapshot;
import de.dytanic.cloudnet.driver.service.ServiceTask;
import de.dytanic.cloudnet.ext.bridge.player.ICloudPlayer;
import de.dytanic.cloudnet.ext.bridge.player.NetworkServiceInfo;

import java.util.UUID;

public interface CloudUser {

    public NetworkServiceInfo moveToPlayer(UUID player);

    public void moveToPlayerTeleport(UUID uuid);

    public void move(UUID service);

    public void move(ServiceTask service);

    public void move(ServiceInfoSnapshot service);

    public void move(String service);

    public ICloudPlayer getCloudPlayer();

    public UUID getUuid();
}
