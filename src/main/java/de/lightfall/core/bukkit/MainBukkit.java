package de.lightfall.core.bukkit;

import de.lightfall.core.Util;
import net.luckperms.api.LuckPerms;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class MainBukkit extends JavaPlugin {

    private LuckPerms api;

    @Override
    public void onEnable() {
        getLogger().info(Util.getLogo());
        RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
        if (provider != null) {
            this.api = provider.getProvider();
        } else {
            getLogger().warning("LuckPerms cannot been found, disabling the plugin.");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        Bukkit.getPluginManager().registerEvents(new ChatManager(api), this);
    }
}
