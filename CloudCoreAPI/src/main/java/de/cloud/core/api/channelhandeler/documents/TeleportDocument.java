package de.cloud.core.api.channelhandeler.documents;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class TeleportDocument extends Document {

    private UUID uuid;
    private TeleportType teleportType;
    private UUID targetUuid;
    private LocationDocument targetPosition;

    public TeleportDocument(UUID uuid, LocationDocument targetPosition) {
        this.uuid = uuid;
        this.teleportType = TeleportType.LOCATION;
        this.targetPosition = targetPosition;
    }

    public TeleportDocument(UUID uuid, UUID targetUuid) {
        this.uuid = uuid;
        this.teleportType = TeleportType.PLAYER;
        this.targetUuid = targetUuid;
    }
}
