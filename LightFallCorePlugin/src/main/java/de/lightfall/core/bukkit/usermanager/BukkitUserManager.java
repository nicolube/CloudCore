package de.lightfall.core.bukkit.usermanager;

import de.lightfall.core.api.bukkit.events.PlayerLoginSuccessEvent;
import de.lightfall.core.api.usermanager.ICloudUser;
import de.lightfall.core.api.usermanager.IUserManager;
import de.lightfall.core.bukkit.MainBukkit;
import de.lightfall.core.models.UserInfoModel;
import de.lightfall.core.models.UserModeInfoModel;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class BukkitUserManager implements IUserManager, Listener {

    @Getter
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
            try {
                final UserInfoModel playerInfo = this.plugin.getPlayerDao().queryBuilder().where().eq("uuid", uuid).queryForFirst();
                System.out.println(playerInfo);
                final BukkitCloudUser bukkitCloudUser = new BukkitCloudUser(p, playerInfo.getId(), this);
                this.userMap.put(uuid, bukkitCloudUser);
                if (this.plugin.getMode() != null)
                    this.plugin.getPlayerModeDao().create(new UserModeInfoModel(playerInfo, this.plugin.getMode()));
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        });
    }

    @EventHandler
    public void onLogin(PlayerLoginEvent event) {
        final Player player = event.getPlayer();
        CompletableFuture.runAsync(() -> {
            try {
                final UserInfoModel playerInfo = this.plugin.getPlayerDao().queryBuilder().where().eq("uuid", player.getUniqueId()).queryForFirst();
                final BukkitCloudUser bukkitCloudUser = new BukkitCloudUser(player, playerInfo.getId(), this);
                final Locale locale = Locale.forLanguageTag(playerInfo.getLocale());
                this.plugin.getCommandManager().setPlayerLocale(player, locale);

                if (!event.getResult().equals(PlayerLoginEvent.Result.ALLOWED)) return;
                this.userMap.put(player.getUniqueId(), bukkitCloudUser);
                if (this.plugin.getMode() != null)
                    this.plugin.getPlayerModeDao().create(new UserModeInfoModel(playerInfo, this.plugin.getMode()));
                Bukkit.getPluginManager().callEvent(new PlayerLoginSuccessEvent(event, bukkitCloudUser));
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });
    }

    @EventHandler
    public void onDisconnect(PlayerQuitEvent event) {
        this.userMap.remove(event.getPlayer().getUniqueId());
    }

    @Override
    public ICloudUser getUser(UUID uuid) {
        final BukkitCloudUser bukkitCloudUser = this.userMap.get(uuid);
        if (bukkitCloudUser != null)
            return bukkitCloudUser;
        // Todo offline load
        throw new NotImplementedException();
    }
}
