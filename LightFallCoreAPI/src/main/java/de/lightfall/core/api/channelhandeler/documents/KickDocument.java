package de.lightfall.core.api.channelhandeler.documents;


import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
public class KickDocument extends Document {

    private UUID uuid;
    private String reason;
    private KickReason kickReason;

    public KickDocument(UUID uuid, String reason, KickReason kickReason) {
        this.uuid = uuid;
        this.reason = reason;
        this.kickReason = kickReason;
    }
}
