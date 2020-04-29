package de.lightfall.core.bungee;

import de.dytanic.cloudnet.driver.event.EventListener;
import de.dytanic.cloudnet.driver.event.events.channel.ChannelMessageReceiveEvent;
import de.lightfall.core.api.channelhandeler.ChannelHandler;
import de.lightfall.core.api.channelhandeler.documents.Document;
import de.lightfall.core.api.channelhandeler.documents.KickDocument;
import de.lightfall.core.bukkit.MainBukkit;
import io.netty.util.internal.SuppressJava6Requirement;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.net.ProxySelector;

public class BungeeChannelHandler extends ChannelHandler {

    private final MainBungee plugin;

    public BungeeChannelHandler(MainBungee plugin) {
        super();
        this.plugin = plugin;
    }

    @Override
    @EventListener
    public void channelHandler(ChannelMessageReceiveEvent event) {
        super.channelHandler(event);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void receive(Document document) {
        if (document instanceof KickDocument) {
            KickDocument kickDocument = (KickDocument) document;

            if (ProxyServer.getInstance().getPlayer(kickDocument.getUuid()) != null) {
                final ProxiedPlayer proxiedPlayer = ProxyServer.getInstance().getPlayer(kickDocument.getUuid());
                this.plugin.getLogger().info(proxiedPlayer.getName() + " (" + proxiedPlayer.getUniqueId().toString() + ") got kicked (" + kickDocument.getReason().toString() + ")");
                proxiedPlayer.disconnect(kickDocument.getReason());
            }

            return;
        }
    }
}
