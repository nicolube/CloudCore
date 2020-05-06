package de.lightfall.core.api.bukkit.events;

import de.lightfall.core.api.usermanager.ICloudUser;
import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerLoginEvent;

public class PlayerLoginSuccessEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    @Getter
    private PlayerLoginEvent event;
    @Getter
    private ICloudUser cloudUser;

    public PlayerLoginSuccessEvent(PlayerLoginEvent event, ICloudUser cloudUser) {
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
