package de.cloud.core.api.config;

import lombok.Data;

import java.util.Map;

@Data
public class Config {

    private DatabaseConfig database;
    private Map<String, String[]> chatColorConfig;
    private de.cloud.core.com.client.Config comConfig;

}
