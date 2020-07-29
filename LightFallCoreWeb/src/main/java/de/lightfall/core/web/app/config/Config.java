package de.lightfall.core.web.app.config;

import de.lightfall.core.api.config.DatabaseConfig;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Config {
    private DatabaseConfig database;
    private String baseUrl;
}
