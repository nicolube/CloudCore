package de.lightfall.core.bungee;

import de.dytanic.cloudnet.driver.event.EventListener;
import de.dytanic.cloudnet.driver.event.events.channel.ChannelMessageReceiveEvent;
import de.lightfall.core.api.channelhandeler.ChannelHandler;
import de.lightfall.core.api.channelhandeler.documents.ConfigDocument;
import de.lightfall.core.api.channelhandeler.documents.Document;

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
        return;
    }
}
