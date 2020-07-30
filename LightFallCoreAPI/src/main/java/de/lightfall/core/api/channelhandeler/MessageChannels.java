package de.lightfall.core.api.channelhandeler;

import lombok.Getter;

public enum MessageChannels {
    DEFAULT();

    @Getter
    private final String channelName;

    MessageChannels() {
        this.channelName = "lightfall-core";
    }
}
