package de.lightfall.core;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Getter;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

public class Util {

    @Getter
    private static String logo ;
    @Getter
    private static Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    static {
        logo = new BufferedReader(new InputStreamReader(Util.class.getResourceAsStream("/resources/logo.txt")))
                .lines().parallel().collect(Collectors.joining("\n"));
    }
}
