package de.cloud.core.api.channelhandeler.documents;


import co.aikar.commands.MessageType;
import de.cloud.core.api.message.IMessageKeyProvider;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class MessageDocument extends Document {

    private UUID uuid;
    private IMessageKeyProvider messageKey;
    private String[] replacements;
    private MessageType type;

    public MessageDocument(UUID uuid, MessageType type, IMessageKeyProvider messageKey, String... replacements) {
        this.uuid = uuid;
        this.messageKey = messageKey;
        this.replacements = replacements;
        this.type = type;
    }
}
