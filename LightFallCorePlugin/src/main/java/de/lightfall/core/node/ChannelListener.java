package de.lightfall.core.node;


import de.dytanic.cloudnet.driver.event.EventListener;
import de.dytanic.cloudnet.driver.event.events.channel.ChannelMessageReceiveEvent;

public class ChannelListener {

    @EventListener
    public void onCall(ChannelMessageReceiveEvent event) {
        System.out.println("\n\n\n");
        System.out.println(event.getData().toJson());
    }
}
