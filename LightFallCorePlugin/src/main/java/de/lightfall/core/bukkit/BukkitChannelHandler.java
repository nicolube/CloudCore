package de.lightfall.core.bukkit;

import de.dytanic.cloudnet.driver.event.EventListener;
import de.dytanic.cloudnet.driver.event.events.channel.ChannelMessageReceiveEvent;
import de.lightfall.core.api.bukkit.events.ReloadEvent;
import de.lightfall.core.api.channelhandeler.ChannelHandler;
import de.lightfall.core.api.channelhandeler.documents.*;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

public class BukkitChannelHandler extends ChannelHandler {
    private final MainBukkit plugin;

    public BukkitChannelHandler(MainBukkit plugin) {
        super();
        this.plugin = plugin;
    }

    @Override
    @EventListener
    public void channelHandler(ChannelMessageReceiveEvent event) {
        super.channelHandler(event);
    }

    @Override
    public void receive(Document document) {
        if (document instanceof TeleportDocument) {
            Player target = Bukkit.getPlayer(((TeleportDocument) document).getTargetUuid());
            if (target == null || !target.isOnline()) return;
            Player player = Bukkit.getPlayer(((TeleportDocument) document).getUuid());
            if (player != null && player.isOnline()) {
                if (((TeleportDocument) document).getTeleportType().equals(TeleportType.PLAYER)) {
                    player.teleport(target);
                    return;
                }
                player.teleport(BukkitUtil.DocumentToLocation(((TeleportDocument) document).getTargetPosition()));
                return;
            }
            this.plugin.getEventBasedExecutions().scheduleExecution((EventBasedExecutions.EventExecutorTask<PlayerJoinEvent>) event -> {
                if (!event.getPlayer().getUniqueId().equals(((TeleportDocument) document).getUuid())) return false;
                if (((TeleportDocument) document).getTeleportType().equals(TeleportType.PLAYER)) {
                    event.getPlayer().teleport(target);
                    return true;
                }
                event.getPlayer().teleport(BukkitUtil.DocumentToLocation(((TeleportDocument) document).getTargetPosition()));
                return true;
            });
            return;
        }
        if (document instanceof MessageDocument) {
            MessageDocument messageDocument = (MessageDocument) document;
            final Player player = Bukkit.getPlayer(messageDocument.getUuid());
            if (player == null || !player.isOnline()) return;
            this.plugin.getCommandManager().getCommandIssuer(player).sendMessage(messageDocument.getType(), messageDocument.getMessageKey(), messageDocument.getReplacements());
            return;
        }
        if (document instanceof ConfigDocument) {
            ConfigDocument configDocument = (ConfigDocument) document;
            this.plugin.onConfigure(configDocument.getConfig());
            return;
        }
        if (document instanceof ReloadDocument) {
            ReloadDocument reloadDocument = (ReloadDocument) document;
            if (reloadDocument.getType() == ReloadType.MESSAGES)
                CompletableFuture.runAsync(() -> {
                    this.plugin.loadMessages();
                    Bukkit.getPluginManager().callEvent(new ReloadEvent(reloadDocument.getType()));
                    this.plugin.getLogger().log(Level.FINE, "Messages reloaded");
                });
            return;
        }
    }
}
