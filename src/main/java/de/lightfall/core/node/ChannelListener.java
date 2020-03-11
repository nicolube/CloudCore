package de.lightfall.core.node;

import de.dytanic.cloudnet.driver.event.EventListener;
import de.dytanic.cloudnet.driver.event.events.channel.ChannelMessageReceiveEvent;

public class ChannelListener {

    @EventListener
    public void handleReciveMessage(ChannelMessageReceiveEvent event) {
        if (!event.getChannel().equals("backend")) {
            return;
        }
    }
}
