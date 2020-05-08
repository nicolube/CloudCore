package de.lightfall.core.api.config;

import lombok.Data;

import java.util.Map;

@Data
public class Config {

    private DatabaseConfig database;
    private Map<String, String[]> chatColorConfig;
}
