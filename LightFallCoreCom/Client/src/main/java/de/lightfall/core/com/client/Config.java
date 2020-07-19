package de.lightfall.core.com.client;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class Config {
    
    private String host;
    private int port;

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }
}
