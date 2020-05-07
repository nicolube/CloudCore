package de.lightfall.core.api.channelhandeler;

import de.lightfall.core.api.channelhandeler.documents.ConfigDocument;
import de.lightfall.core.api.channelhandeler.documents.ConfigRequestDocument;
import de.lightfall.core.api.channelhandeler.documents.LocationDocument;
import de.lightfall.core.api.channelhandeler.documents.TeleportDocument;
import lombok.Getter;

@Getter
public enum  DocumentRegister {
    TELEPORT(TeleportDocument.class),
    LOCATION(LocationDocument.class),
    REQUEST_CONFIG(ConfigRequestDocument.class),
    CONFIG(ConfigDocument.class);

    private Class clazz;

    DocumentRegister(Class clazz) {
        this.clazz = clazz;
    }
}
