package de.lightfall.core.api.bukkit.events;

import de.lightfall.core.api.usermanager.ICloudUser;
import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinSuccessEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    @Getter
    private PlayerJoinEvent event;
    @Getter
    private ICloudUser cloudUser;

    public PlayerJoinSuccessEvent(PlayerJoinEvent event, ICloudUser cloudUser) {
        this.event = event;
        this.cloudUser = cloudUser;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
