package de.lightfall.core.bukkit;

import de.lightfall.core.api.Util;
import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class MainBukkit extends JavaPlugin {
    @Getter
    private EventBasedExecutions eventBasedExecutions;
    private BukkitChannelHandler channelHandler;

    @Override
    public void onEnable() {
        getLogger().info(Util.getLogo());
        getLogger().info("Create Event based executor...");
        this.eventBasedExecutions = new EventBasedExecutions(this);
        getLogger().info("Create channel handler executor...");
        this.channelHandler = new BukkitChannelHandler(this);
    }
}
