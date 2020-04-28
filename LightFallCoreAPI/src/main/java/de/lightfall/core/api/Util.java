package de.lightfall.core.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Getter;

import java.io.*;
import java.util.stream.Collectors;

public class Util {

    @Getter
    private static String logo ;
    @Getter
    private static Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static void copyOutOfJarFile(String srcPath, File outFile) {
        try {
            InputStream inputStream = Util.class.getResourceAsStream(srcPath);
            FileOutputStream outputStream = new FileOutputStream(outFile);
            byte[] buffer = new byte[inputStream.available()];
            inputStream.read(buffer);
            inputStream.close();
            outputStream.write(buffer);
            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static {
        logo = new BufferedReader(new InputStreamReader(Util.class.getResourceAsStream("/resources/logo.txt")))
                .lines().parallel().collect(Collectors.joining("\n"));
    }
}
