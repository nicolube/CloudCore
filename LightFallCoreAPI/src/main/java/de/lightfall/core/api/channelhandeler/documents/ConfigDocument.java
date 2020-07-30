package de.lightfall.core.api.channelhandeler.documents;

import de.lightfall.core.api.config.Config;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class ConfigDocument extends Document {
    private Config config;

    public ConfigDocument(Config config) {
        this.config = config;
    }
}
