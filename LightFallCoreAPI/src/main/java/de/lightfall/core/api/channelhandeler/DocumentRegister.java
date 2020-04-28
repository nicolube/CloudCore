package de.lightfall.core.api.channelhandeler;

import de.lightfall.core.api.channelhandeler.documents.KickDocument;
import de.lightfall.core.api.channelhandeler.documents.LocationDocument;
import de.lightfall.core.api.channelhandeler.documents.TeleportDocument;
import lombok.Getter;

@Getter
public enum  DocumentRegister {
    TELEPORT(TeleportDocument.class),
    KICK(KickDocument.class),
    LOCATION(LocationDocument.class);

    private Class clazz;

    DocumentRegister(Class clazz) {
        this.clazz = clazz;
    }
}
