package de.lightfall.core.bungee.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.bungee.contexts.OnlinePlayer;
import de.dytanic.cloudnet.driver.CloudNetDriver;
import de.dytanic.cloudnet.driver.service.ServiceInfoSnapshot;
import de.dytanic.cloudnet.ext.bridge.BridgePlayerManager;
import de.lightfall.core.api.channelhandeler.ChannelHandler;
import de.lightfall.core.api.channelhandeler.documents.TeleportDocument;
import de.lightfall.core.bungee.MainBungee;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.UUID;

@CommandAlias("test")
public class TestCommand extends BaseCommand {
    private final MainBungee plugin;

    public TestCommand(MainBungee plugin) {
        this.plugin = plugin;
    }

    @Subcommand("teleport")
    public void onTeleport(ProxiedPlayer player, OnlinePlayer target) {
        final UUID serviceUuid = BridgePlayerManager.getInstance().getOnlinePlayer(player.getUniqueId()).getConnectedService().getUniqueId();
        final ServiceInfoSnapshot cloudService = CloudNetDriver.getInstance().getCloudServiceProvider().getCloudService(serviceUuid);
        System.out.println(cloudService.getServiceId().getName());
        ChannelHandler.send(cloudService, new TeleportDocument(player.getUniqueId(), target.getPlayer().getUniqueId()));
    }
}
