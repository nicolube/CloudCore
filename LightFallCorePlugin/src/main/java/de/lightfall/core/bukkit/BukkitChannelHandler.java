package de.lightfall.core.bukkit;

import de.dytanic.cloudnet.driver.CloudNetDriver;
import de.dytanic.cloudnet.driver.event.EventListener;
import de.dytanic.cloudnet.driver.event.events.channel.ChannelMessageReceiveEvent;
import de.lightfall.core.api.channelhandeler.ChannelHandler;
import de.lightfall.core.api.channelhandeler.documents.Document;
import de.lightfall.core.api.channelhandeler.documents.TeleportDocument;
import de.lightfall.core.api.channelhandeler.documents.TeleportType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;

public class BukkitChannelHandler extends ChannelHandler {
    private final MainBukkit plugin;

    public BukkitChannelHandler(MainBukkit plugin) {
        super();
        this.plugin = plugin;
    }

    @Override @EventListener
    public void channelHandler(ChannelMessageReceiveEvent event) {
        super.channelHandler(event);
    }

    @Override
    public void receive(Document document) {
        Bukkit.broadcastMessage("Received message. Type: "+document.getClass().getName());
        if (document instanceof TeleportDocument) {
            Player player = Bukkit.getPlayer(((TeleportDocument) document).getUuid());
            if (player.isOnline()) {
                if (((TeleportDocument) document).getTeleportType().equals(TeleportType.PLAYER)) {
                    player.teleport(Bukkit.getPlayer(((TeleportDocument) document).getTargetUuid()));
                    return;
                }
                player.teleport(BukkitUtil.DocumentToLocation(((TeleportDocument) document).getTargetPosition()));
                return;
            }
            this.plugin.getEventBasedExecutions().scheduleExecution(new EventBasedExecutions.EventExecutorTask<PlayerJoinEvent>() {
                @Override
                public boolean execute(PlayerJoinEvent event) {
                    if (!event.getPlayer().getUniqueId().equals(((TeleportDocument) document).getUuid())) return false;
                    if (((TeleportDocument) document).getTeleportType().equals(TeleportType.PLAYER)) {
                        player.teleport(Bukkit.getPlayer(((TeleportDocument) document).getTargetUuid()));
                        return true;
                    }
                    player.teleport(BukkitUtil.DocumentToLocation(((TeleportDocument) document).getTargetPosition()));
                    return true;
                }
            });
            return;

        }
    }
}
