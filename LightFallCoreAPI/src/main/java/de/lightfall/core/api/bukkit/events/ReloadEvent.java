package de.lightfall.core.api.bukkit.events;

import de.lightfall.core.api.channelhandeler.documents.ReloadType;
import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ReloadEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    @Getter
    private final ReloadType type;

    public ReloadEvent(ReloadType type) {
        super(true);
        this.type = type;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
