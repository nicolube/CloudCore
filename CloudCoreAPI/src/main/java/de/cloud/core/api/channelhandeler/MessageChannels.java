package de.cloud.core.api.channelhandeler;

import lombok.Getter;

public enum MessageChannels {
    DEFAULT();

    @Getter
    private final String channelName;

    MessageChannels() {
        this.channelName = "cloud-core";
    }
}
