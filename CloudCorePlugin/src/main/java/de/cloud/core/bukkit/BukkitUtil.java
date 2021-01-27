package de.cloud.core.bukkit;

import de.cloud.core.api.Util;
import de.cloud.core.api.channelhandeler.documents.LocationDocument;
import org.bukkit.Bukkit;
import org.bukkit.Location;

public class BukkitUtil extends Util {

    public static Location DocumentToLocation(LocationDocument document) {
        if (document.getPitch() == 0 && document.getYaw() == 0)
            return new Location(Bukkit.getWorld(document.getWorld()), document.getX(), document.getY(), document.getY());
        return new Location(Bukkit.getWorld(document.getWorld()), document.getX(), document.getY(), document.getY(), document.getPitch(), document.getYaw());
    }

    public static LocationDocument LocationToDocument(Location location) {
        if (location.getPitch() == 0 && location.getYaw() == 0)
            return new LocationDocument(location.getWorld().getName(), location.getX(), location.getY(), location.getZ());
        return new LocationDocument(location.getWorld().getName(), location.getX(), location.getY(), location.getZ(), location.getPitch(), location.getYaw());
    }
}
