package de.lightfall.core.api.channelhandeler.documents;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ReloadDocument extends Document {
    private ReloadType type;

    public ReloadDocument(ReloadType type) {
        this.type = type;
    }
}
