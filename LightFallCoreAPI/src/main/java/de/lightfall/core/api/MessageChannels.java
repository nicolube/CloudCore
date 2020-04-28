package de.lightfall.core.api;

import lombok.Getter;

public enum MessageChannels {
    DEFAULT("lightfall-core");

    @Getter
    private String channelName;

    MessageChannels(String channelName) {
        this.channelName = channelName;
    }
}
