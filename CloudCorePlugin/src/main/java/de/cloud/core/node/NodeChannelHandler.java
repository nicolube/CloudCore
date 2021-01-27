package de.cloud.core.node;

import de.cloud.core.api.channelhandeler.ChannelHandler;
import de.cloud.core.api.channelhandeler.documents.ConfigDocument;
import de.cloud.core.api.channelhandeler.documents.ConfigRequestDocument;
import de.cloud.core.api.channelhandeler.documents.Document;
import de.dytanic.cloudnet.driver.CloudNetDriver;
import de.dytanic.cloudnet.driver.event.EventListener;
import de.dytanic.cloudnet.driver.network.INetworkChannel;
import de.dytanic.cloudnet.driver.network.protocol.IPacket;
import de.dytanic.cloudnet.driver.network.protocol.IPacketListener;
import de.dytanic.cloudnet.driver.service.ServiceInfoSnapshot;
import de.dytanic.cloudnet.event.network.NetworkChannelAuthCloudServiceSuccessEvent;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.util.concurrent.CompletableFuture;

public class NodeChannelHandler extends ChannelHandler implements IPacketListener {
    private final MainModule module;

    public NodeChannelHandler(MainModule module) {
        super();
        this.module = module;
    }

    @EventListener
    public void onReceive(NetworkChannelAuthCloudServiceSuccessEvent event) {
        event.getChannel().getPacketRegistry().addListener(100, this);
    }

    @Override
    public void handle(INetworkChannel channel, IPacket packet) throws Exception {
        final ByteArrayInputStream bis = new ByteArrayInputStream(packet.getBody());
        final ObjectInputStream objectInputStream = new ObjectInputStream(bis);
        final Document document = (Document) objectInputStream.readObject();
        final String serviceTaskSender = packet.getHeader().getString("sender");
        CompletableFuture.runAsync(() -> {
            ServiceInfoSnapshot cloudServiceByName = CloudNetDriver.getInstance().getCloudServiceProvider().getCloudServiceByName(serviceTaskSender);
            receive(cloudServiceByName, document);
        });
    }

    @Override
    public native void receive(Document document);

    private void receive(ServiceInfoSnapshot sender, Document document) {
        if (document instanceof ConfigRequestDocument) {
            this.module.getLogger().info(sender.getServiceId().getName() + " requested configuration");
            send(sender, new ConfigDocument(this.module.getMConfig()));
        }
    }

}