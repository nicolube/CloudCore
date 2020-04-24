package de.lightfall.core.api.channelhandeler.documents;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
public class TeleportDocument {

    public TeleportDocument(UUID uuid, TeleportType teleportType, TeleportDocument targetPosition) {
        this.uuid = uuid;
        this.teleportType = teleportType;
        this.targetPosition = targetPosition;
    }

    public TeleportDocument(UUID uuid, TeleportType teleportType, UUID targetUuid) {
        this.uuid = uuid;
        this.teleportType = teleportType;
        this.targetUuid = targetUuid;
    }

    private UUID uuid;
    private TeleportType teleportType;
    private UUID targetUuid;
    private TeleportDocument targetPosition;
}
