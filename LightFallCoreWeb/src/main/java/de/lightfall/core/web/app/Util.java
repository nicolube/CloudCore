package de.lightfall.core.web.app;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Getter;

import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Util {
    @Getter
    private static Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static UUID uuiFromString(String uuid) {
        if (!uuid.contains("-")) {
            uuid = uuid.replaceFirst( "([0-9a-fA-F]{8})([0-9a-fA-F]{4})([0-9a-fA-F]{4})([0-9a-fA-F]{4})([0-9a-fA-F]+)", "$1-$2-$3-$4-$5" );
        }
        return UUID.fromString(uuid);
    }
}
