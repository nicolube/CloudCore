package de.cloud.core.api.channelhandeler;

import de.cloud.core.api.channelhandeler.documents.*;
import lombok.Getter;

@Getter
public enum  DocumentRegister {
    TELEPORT(TeleportDocument.class),
    LOCATION(LocationDocument.class),
    REQUEST_CONFIG(ConfigRequestDocument.class),
    CONFIG(ConfigDocument.class),
    MESSAGE(MessageDocument.class),
    RELOAD(ReloadDocument.class);

    private final Class clazz;

    DocumentRegister(Class clazz) {
        this.clazz = clazz;
    }
}
