package de.lightfall.core.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import net.md_5.bungee.api.ChatColor;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;
import java.util.stream.Collectors;

public class Util {

    // This is very sketchy
    protected static CoreAPI coreInstance = null;

    @Getter
    private static String logo;
    @Getter
    private static Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    @Setter
    private static String banFormat;

    public static void copyOutOfJarFile(@NonNull String srcPath, @NonNull File outFile) {
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

    public static String formatDate(@NonNull Date date, @NonNull Locale locale) {
        DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.MEDIUM, locale);
        return dateFormat.format(date);
    }

    public static String formatBan(Date date, @NonNull String reason, Locale locale) {
        String length = date != null ? formatDate(date, locale) : "permanent";
        return ChatColor.translateAlternateColorCodes('&', String.format(Util.banFormat, reason, length));
    }

    static {
        logo = new BufferedReader(new InputStreamReader(Util.class.getResourceAsStream("/resources/logo.txt")))
                .lines().parallel().collect(Collectors.joining("\n"));
    }
}
