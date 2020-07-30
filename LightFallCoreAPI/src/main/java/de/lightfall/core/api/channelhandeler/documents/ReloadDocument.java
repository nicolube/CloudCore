package de.lightfall.core.api.channelhandeler.documents;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class ReloadDocument extends Document {
    private ReloadType type;

    public ReloadDocument(ReloadType type) {
        this.type = type;
    }
}
