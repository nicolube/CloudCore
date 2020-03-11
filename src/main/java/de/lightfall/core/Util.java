package de.lightfall.core;

import lombok.Getter;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

public class Util {

    @Getter
    private static String logo ;

    static {
        logo = new BufferedReader(new InputStreamReader(Util.class.getResourceAsStream("/resources/logo.txt")))
                .lines().parallel().collect(Collectors.joining("\n"));
        System.out.println(logo);
    }
}
