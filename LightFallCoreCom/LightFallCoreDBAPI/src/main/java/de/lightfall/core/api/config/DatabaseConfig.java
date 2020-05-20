package de.lightfall.core.api.config;

import lombok.Data;

@Data
public class DatabaseConfig {

    private String url;
    private String user;
    private String password;

}
