package de.lightfall.core.bukkit.usermanager;

import de.lightfall.core.api.usermanager.CloudUser;
import de.lightfall.core.api.usermanager.UserManager;
import de.lightfall.core.bukkit.MainBukkit;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BukkitUserManager implements UserManager, Listener {

    private final MainBukkit plugin;
    private Map<UUID, BukkitCloudUser> userMap;

    public BukkitUserManager(MainBukkit plugin) {
        this.plugin = plugin;
        this.userMap = new HashMap<>();
        onLoad();
    }


    public void onLoad() {
        this.userMap.clear();
        Bukkit.getOnlinePlayers().forEach(p -> {
            final UUID uuid = p.getUniqueId();
            final BukkitCloudUser bukkitCloudUser = new BukkitCloudUser(p);
            this.userMap.put(uuid, bukkitCloudUser);
        });
    }

    @EventHandler
    public void onLogin(PlayerLoginEvent event) {
        final Player player = event.getPlayer();
        final BukkitCloudUser bukkitCloudUser = new BukkitCloudUser(player);
        // Todo ban cancel / whitelist
        if (!event.getResult().equals(PlayerLoginEvent.Result.ALLOWED)) return;
        this.userMap.put(player.getUniqueId(), bukkitCloudUser);
    }

    @EventHandler
    public void onDisconnect(PlayerQuitEvent event) {
        this.userMap.remove(event.getPlayer().getUniqueId());
    }

    @Override
    public CloudUser getUser(UUID uuid) {
        final BukkitCloudUser bukkitCloudUser = this.userMap.get(uuid);
        if (bukkitCloudUser != null)
            return bukkitCloudUser;
        // Todo offline load
        throw new NotImplementedException();
    }
}
