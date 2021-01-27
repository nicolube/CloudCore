package de.cloud.core.web.app.config;

import de.cloud.core.api.config.DatabaseConfig;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Config {
    private DatabaseConfig database;
    private String baseUrl;
}
