package de.lightfall.core.node;

import de.dytanic.cloudnet.driver.CloudNetDriver;
import de.dytanic.cloudnet.driver.event.EventListener;
import de.dytanic.cloudnet.driver.network.INetworkChannel;
import de.dytanic.cloudnet.driver.network.protocol.IPacket;
import de.dytanic.cloudnet.driver.network.protocol.IPacketListener;
import de.dytanic.cloudnet.driver.service.ServiceInfoSnapshot;
import de.dytanic.cloudnet.event.network.NetworkChannelAuthCloudServiceSuccessEvent;
import de.lightfall.core.api.channelhandeler.ChannelHandler;
import de.lightfall.core.api.channelhandeler.documents.ConfigDocument;
import de.lightfall.core.api.channelhandeler.documents.ConfigRequestDocument;
import de.lightfall.core.api.channelhandeler.documents.Document;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.util.UUID;

public class NodeChannelHandler extends ChannelHandler implements IPacketListener {
    private MainModule module;

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
        final UUID serviceTaskSender = UUID.fromString(packet.getHeader().getString("sender"));
        CloudNetDriver.getInstance().getCloudServiceProvider().getCloudServiceAsync(serviceTaskSender)
                .onComplete(serviceInfoSnapshot -> receive(serviceInfoSnapshot, document));
        System.out.println(document.getClass());
    }

    @Override
    public native void receive(Document document);

    private void receive(ServiceInfoSnapshot sender, Document document) {
        if (document instanceof ConfigRequestDocument) {
            this.module.getLogger().info(sender.getServiceId().getName()+" requested configuration");
            send(sender, new ConfigDocument(this.module.getMConfig()));
        }
    }

}