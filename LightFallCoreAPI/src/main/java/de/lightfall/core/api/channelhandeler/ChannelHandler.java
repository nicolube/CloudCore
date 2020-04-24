package de.lightfall.core.api.channelhandeler;

import de.dytanic.cloudnet.common.document.gson.JsonDocument;
import de.dytanic.cloudnet.driver.CloudNetDriver;
import de.dytanic.cloudnet.driver.event.EventListener;
import de.dytanic.cloudnet.driver.event.events.channel.ChannelMessageReceiveEvent;
import de.dytanic.cloudnet.driver.service.ServiceInfoSnapshot;
import de.lightfall.core.api.MessageChannels;
import de.lightfall.core.api.channelhandeler.documents.Document;

public abstract class ChannelHandler {
    public ChannelHandler() {
        CloudNetDriver.getInstance().getEventManager().registerListener(this);
    }

    @EventListener
    public void channelHandler(ChannelMessageReceiveEvent event) {
        if (event.getChannel() != MessageChannels.DEFAULT.getChannelName()) return;
        final String key = event.getMessage().toLowerCase();
        for (DocumentRegister value : DocumentRegister.values()) {
            if (value.getKey().equals(key)) {
                receive(event.getData().toInstanceOf((Class<? extends Document>) value.getClazz()));
                return;
            }
        }
    }

    public abstract void receive(Document document);

    public static void send(ServiceInfoSnapshot cloudService, Document document) {
        for (DocumentRegister value : DocumentRegister.values()) {
            if (document.getClass().equals(value.getClazz())) {
                CloudNetDriver.getInstance().getMessenger().sendChannelMessage(cloudService,
                        MessageChannels.DEFAULT.getChannelName(), value.getKey(), new JsonDocument(document));
                return;
            }

        }
    }
}
