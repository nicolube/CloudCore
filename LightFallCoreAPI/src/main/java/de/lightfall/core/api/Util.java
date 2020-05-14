package de.lightfall.core.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import net.md_5.bungee.api.ChatColor;

import java.io.*;
import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
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

    public static String formatDate(Date date, @NonNull Locale locale) {
        if (date == null) return "permanent";
        DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM, locale);
        return dateFormat.format(date);
    }

    public static String formatBan(Date date, @NonNull String reason, Locale locale) {
        String length = formatDate(date, locale);
        return ChatColor.translateAlternateColorCodes('&', String.format(Util.banFormat, reason, length));
    }

    public static Long stringToMilesPhrase(String string) {
        try {
            if (string.contains(",")) {
                long timeInSeconds = 0L;
                for (String part : string.split(",")) {
                    if (string.contains("s")) {
                        timeInSeconds += Long.parseLong(string.split("s")[0]);
                    }
                    if (string.contains("m")) {
                        timeInSeconds += TimeUnit.MINUTES.toSeconds(Long.parseLong(string.split("m")[0]));
                    }
                    if (string.contains("h")) {
                        timeInSeconds += TimeUnit.HOURS.toSeconds(Long.parseLong(string.split("h")[0]));
                    }
                    if (string.contains("d")) {
                        timeInSeconds += TimeUnit.DAYS.toSeconds(Long.parseLong(string.split("d")[0]));
                    }
                    if (string.contains("mo")) {
                        timeInSeconds += TimeUnit.DAYS.toSeconds(Long.parseLong(string.split("mo")[0]) * 30);
                    }
                }
                if (timeInSeconds == 0L || timeInSeconds < 0L) {
                    return null;
                }
                return timeInSeconds;
            } else {
                if (string.contains("s")) {
                    return Long.parseLong(string.split("s")[0]);
                }
                if (string.contains("m")) {
                    return TimeUnit.MINUTES.toSeconds(Long.parseLong(string.split("m")[0]));
                }
                if (string.contains("h")) {
                    return TimeUnit.HOURS.toSeconds(Long.parseLong(string.split("h")[0]));
                }
                if (string.contains("d")) {
                    return TimeUnit.DAYS.toSeconds(Long.parseLong(string.split("d")[0]));
                }
                if (string.contains("mo")) {
                    return TimeUnit.DAYS.toSeconds(Long.parseLong(string.split("mo")[0]) * 30);
                }
                return null;
            }
        } catch (Exception ignored) {
            return null;
        }
    }

    static {
        logo = new BufferedReader(new InputStreamReader(Util.class.getResourceAsStream("/resources/logo.txt")))
                .lines().parallel().collect(Collectors.joining("\n"));
    }
}
