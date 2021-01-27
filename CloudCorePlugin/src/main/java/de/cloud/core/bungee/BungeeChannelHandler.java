package de.cloud.core.bungee;

import de.cloud.core.api.bungee.events.ReloadEvent;
import de.cloud.core.api.channelhandeler.ChannelHandler;
import de.cloud.core.api.channelhandeler.documents.ConfigDocument;
import de.cloud.core.api.channelhandeler.documents.Document;
import de.cloud.core.api.channelhandeler.documents.ReloadDocument;
import de.cloud.core.api.channelhandeler.documents.ReloadType;
import de.dytanic.cloudnet.driver.event.EventListener;
import de.dytanic.cloudnet.driver.event.events.channel.ChannelMessageReceiveEvent;

import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

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
    public void receive(Document document) {
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
                    this.plugin.getProxy().getPluginManager().callEvent(new ReloadEvent(reloadDocument.getType()));
                    this.plugin.getLogger().log(Level.FINE, "Messages reloaded");
                });
            return;
        }
        return;
    }
}
