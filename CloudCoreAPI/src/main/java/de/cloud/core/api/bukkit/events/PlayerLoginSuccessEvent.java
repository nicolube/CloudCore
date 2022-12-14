package de.cloud.core.api.bukkit.events;

import de.cloud.core.api.usermanager.ICloudUser;
import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerLoginEvent;

public class PlayerLoginSuccessEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    @Getter
    private final PlayerLoginEvent event;
    @Getter
    private final ICloudUser cloudUser;

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
