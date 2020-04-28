package de.lightfall.core.bungee.usermanager;

import de.lightfall.core.api.usermanager.CloudUser;
import de.lightfall.core.api.usermanager.UserManager;
import de.lightfall.core.bungee.MainBungee;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.Map;
import java.util.UUID;

public class BungeeUserManager implements UserManager, Listener {

    private MainBungee plugin;

    private Map<UUID, BungeeCloudUser> userMap;

    public BungeeUserManager(MainBungee plugin) {
        this.plugin = plugin;
    }

    public void onLoad() {
        this.userMap.clear();
        this.plugin.getProxy().getPlayers().forEach(p -> {
            final UUID uuid = p.getUniqueId();
            final BungeeCloudUser bungeeCloudUser = new BungeeCloudUser(uuid);
            bungeeCloudUser.setPlayer(p);
            this.userMap.put(uuid, bungeeCloudUser);
        });
    }

    @EventHandler
    public void onLogin(LoginEvent event) {
        final UUID uuid = event.getConnection().getUniqueId();
        final BungeeCloudUser bungeeCloudUser = new BungeeCloudUser(uuid);
        // Todo ban cancel / whitelist
        if (event.isCancelled()) return;
        this.userMap.put(uuid, bungeeCloudUser);
    }

    @EventHandler
    public void onPostLogin(PostLoginEvent event) {
        final ProxiedPlayer player = event.getPlayer();
        this.userMap.get(player.getUniqueId()).setPlayer(player);
    }

    @EventHandler
    public void onDisconnect(PlayerDisconnectEvent event) {
        this.userMap.remove(event.getPlayer().getUniqueId());
    }

    @Override
    public CloudUser getUser(UUID uuid) {
        final BungeeCloudUser bungeeCloudUser = this.userMap.get(uuid);
        if (bungeeCloudUser != null)
            return bungeeCloudUser;
        // Todo offline load
        throw new NotImplementedException();
    }
}
