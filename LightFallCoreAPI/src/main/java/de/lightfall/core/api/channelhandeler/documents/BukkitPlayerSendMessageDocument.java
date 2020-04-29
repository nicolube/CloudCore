package de.lightfall.core.api.channelhandeler.documents;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BukkitPlayerSendMessageDocument extends Document {

    private UUID uuid;
    private String message;
    boolean translateAlternateColorCodes;


}
