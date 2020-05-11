package de.lightfall.core.api.bungee.events;

import de.lightfall.core.api.channelhandeler.documents.ReloadType;
import lombok.Getter;
import net.md_5.bungee.api.plugin.Event;

public class ReloadEvent extends Event {

    @Getter
    private final ReloadType type;

    public ReloadEvent(ReloadType type) {
        this.type = type;
    }
}
