package de.lightfall.core.bukkit;

import de.lightfall.core.Util;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class MainBukkit extends JavaPlugin {


    @Override
    public void onEnable() {
        getLogger().info(Util.getLogo());
    }
}
