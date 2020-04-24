package de.lightfall.core.api.channelhandeler;

import de.lightfall.core.api.channelhandeler.documents.LocationDocument;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum  DocumentRegister {
    TELEPORT("tp", LocationDocument.class);

    private String key;
    private Class clazz;
}
