package de.lightfall.core.api.channelhandeler;

import de.dytanic.cloudnet.common.document.gson.JsonDocument;
import de.dytanic.cloudnet.driver.CloudNetDriver;
import de.dytanic.cloudnet.driver.event.EventListener;
import de.dytanic.cloudnet.driver.event.events.channel.ChannelMessageReceiveEvent;
import de.dytanic.cloudnet.driver.network.protocol.Packet;
import de.dytanic.cloudnet.driver.service.ServiceInfoSnapshot;
import de.dytanic.cloudnet.driver.service.ServiceTask;
import de.dytanic.cloudnet.wrapper.Wrapper;
import de.lightfall.core.api.channelhandeler.documents.Document;
import lombok.SneakyThrows;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;

public abstract class ChannelHandler {

    public ChannelHandler() {
        CloudNetDriver.getInstance().getEventManager().registerListener(this);
    }

    @EventListener
    public void channelHandler(ChannelMessageReceiveEvent event) {
        if (!event.getChannel().equals(MessageChannels.DEFAULT.getChannelName())) return;
        final String key = event.getMessage();
        for (DocumentRegister value : DocumentRegister.values()) {
            if (value.name().equals(key)) {
                receive(event.getData().toInstanceOf((Class<? extends Document>) value.getClazz()));
                return;
            }
        }
    }

    public abstract void receive(Document document);

    public static void send(ServiceInfoSnapshot cloudService, Document document) {
        for (DocumentRegister value : DocumentRegister.values()) {
            if (document.getClass().equals(value.getClazz())) {
                CloudNetDriver.getInstance().getNetworkClient().sendPacket();
                CloudNetDriver.getInstance().getMessenger().sendChannelMessage(cloudService,
                        MessageChannels.DEFAULT.getChannelName(), value.name(), new JsonDocument(document));
                return;
            }

        }
    }

    public static void send(ServiceTask cloudService, Document document) {
        for (DocumentRegister value : DocumentRegister.values()) {
            if (document.getClass().equals(value.getClazz())) {
                CloudNetDriver.getInstance().getNetworkClient().sendPacket();
                CloudNetDriver.getInstance().getMessenger().sendChannelMessage(cloudService,
                        MessageChannels.DEFAULT.getChannelName(), value.name(), new JsonDocument(document));
                return;
            }

        }
    }


    public static void send(Document document) {
        for (DocumentRegister value : DocumentRegister.values()) {
            if (document.getClass().equals(value.getClazz())) {
                CloudNetDriver.getInstance().getNetworkClient().sendPacket();
                CloudNetDriver.getInstance().getMessenger()
                        .sendChannelMessage(MessageChannels.DEFAULT.getChannelName(), value.name(), new JsonDocument(document));
                return;
            }

        }
    }

    @SneakyThrows
    public static void sendToCloud(Document document) {
        for (DocumentRegister value : DocumentRegister.values()) {
            if (document.getClass().equals(value.getClazz())) {
                final ByteArrayOutputStream bos = new ByteArrayOutputStream();
                final ObjectOutputStream objectOutputStream = new ObjectOutputStream(bos);
                objectOutputStream.writeObject(document);
                objectOutputStream.flush();
                bos.flush();
                final byte[] bytes = bos.toByteArray();
                final JsonDocument sender = new JsonDocument().append("sender", Wrapper.getInstance().getServiceId().getName());
                CloudNetDriver.getInstance().getNetworkClient().sendPacket(new Packet(100, sender, bytes));
                return;
            }

        }
    }
}
