package de.lightfall.core.bukkit.usermanager;

import de.lightfall.core.api.bukkit.events.PlayerLoginSuccessEvent;
import de.lightfall.core.bukkit.MainBukkit;
import de.lightfall.core.models.PunishmentModel;
import de.lightfall.core.models.UserInfoModel;
import de.lightfall.core.models.UserModeInfoModel;
import de.lightfall.core.usermanager.UserManager;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class BukkitUserManager extends UserManager implements Listener {

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
                final UserInfoModel playerInfo = this.plugin.getUserInfoDao().queryBuilder().where().eq("uuid", uuid).queryForFirst();
                final BukkitCloudUser bukkitCloudUser = new BukkitCloudUser(p, playerInfo.getId(), this);
                bukkitCloudUser.setLocale(playerInfo.getLocale(), false);
                this.userMap.put(uuid, bukkitCloudUser);
                if (this.plugin.getMode() != null)
                    this.plugin.getUserModeInfoDao().create(new UserModeInfoModel(playerInfo, this.plugin.getMode()));
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
                final UserInfoModel playerInfo = this.plugin.getUserInfoDao().queryBuilder().where().eq("uuid", player.getUniqueId()).queryForFirst();
                final BukkitCloudUser bukkitCloudUser = new BukkitCloudUser(player, playerInfo.getId(), this);
                bukkitCloudUser.setLocale(playerInfo.getLocale(), false);

                if (!event.getResult().equals(PlayerLoginEvent.Result.ALLOWED)) return;
                this.userMap.put(player.getUniqueId(), bukkitCloudUser);
                if (this.plugin.getMode() != null)
                    this.plugin.getUserModeInfoDao().create(new UserModeInfoModel(playerInfo, this.plugin.getMode()));
                Bukkit.getPluginManager().callEvent(new PlayerLoginSuccessEvent(event, bukkitCloudUser));
            } catch (SQLException ignored) {
            }
        });
    }

    @EventHandler
    public void onDisconnect(PlayerQuitEvent event) {
        this.userMap.remove(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        final UUID uuid = event.getPlayer().getUniqueId();
        final BukkitCloudUser user = this.getUser(uuid);
        PunishmentModel activeMute = user.queryUserInfo().getActiveMute();
        if (activeMute == null && this.plugin.getMode() != null) {
            activeMute = this.getUser(uuid).queryUserModeInfo(this.plugin.getMode()).getActiveMute();
        }
        if (activeMute == null) return;
        event.setCancelled(true);
        // Todo add muted message
    }

    @Override
    public BukkitCloudUser getUser(UUID uuid) {
        return this.userMap.get(uuid);
    }
}
