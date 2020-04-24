package de.lightfall.core.bukkit.usermanager;

import de.lightfall.core.api.usermanager.UserManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;

public class BukkitUserManager implements UserManager, Listener {

    @EventHandler
    private void onLogin(PlayerLoginEvent event) {
        if (event.getResult().equals(PlayerLoginEvent.Result.KICK_FULL)) {

        }
    }

}
