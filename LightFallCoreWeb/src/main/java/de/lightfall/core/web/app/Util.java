package de.lightfall.core.web;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Getter;

public class Util {
    @Getter
    private static Gson GSON = new GsonBuilder().setPrettyPrinting().create();
}
