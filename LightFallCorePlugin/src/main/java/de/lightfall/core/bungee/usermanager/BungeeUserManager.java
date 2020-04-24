package de.lightfall.core.bungee.usermanager;

import de.lightfall.core.api.usermanager.UserManager;
import de.lightfall.core.bungee.MainBungee;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class BungeeUserManager implements UserManager, Listener {

    private MainBungee plugin;

    public BungeeUserManager(MainBungee plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(LoginEvent event) {
        this.plugin.getLogger().info(event.getConnection().getUniqueId().toString());
        final TextComponent textComponent = new TextComponent(ChatColor.translateAlternateColorCodes('&', "&eTestMessage\n&bSecond Line"));
        event.setCancelReason(textComponent);
        event.setCancelled(true);
    }
}
