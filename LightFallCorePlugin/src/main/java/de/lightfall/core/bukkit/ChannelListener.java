package de.lightfall.core.bukkit;


import de.dytanic.cloudnet.driver.event.EventListener;
import de.dytanic.cloudnet.driver.event.events.channel.ChannelMessageReceiveEvent;
import de.lightfall.core.api.MessageChannels;

public class ChannelListener {

    @EventListener
    public void onCall(ChannelMessageReceiveEvent event) {
        if (event.getChannel() != MessageChannels.DEFAULT.getChannelName()) return;
        if (event.getMessage() == "tp") {

        }
    }
}
