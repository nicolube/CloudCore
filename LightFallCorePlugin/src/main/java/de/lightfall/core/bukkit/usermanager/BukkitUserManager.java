package de.lightfall.core.bukkit.usermanager;

import co.aikar.commands.MessageType;
import com.j256.ormlite.stmt.UpdateBuilder;
import de.lightfall.core.api.Util;
import de.lightfall.core.api.bukkit.events.PlayerLoginSuccessEvent;
import de.lightfall.core.api.message.CoreMessageKeys;
import de.lightfall.core.bukkit.MainBukkit;
import de.lightfall.core.common.models.PunishmentModel;
import de.lightfall.core.common.models.UserInfoModel;
import de.lightfall.core.common.models.UserModeInfoModel;
import de.lightfall.core.usermanager.UserManager;
import lombok.Getter;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.sql.SQLException;
import java.util.Date;
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
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });
    }

    @EventHandler
    public void onLogin(PlayerLoginEvent event) {
        final Player player = event.getPlayer();
        CompletableFuture.runAsync(() -> {
            try {
                final UserInfoModel userInfo = quarryUserInfo(player.getUniqueId());
                final BukkitCloudUser bukkitCloudUser = new BukkitCloudUser(player, userInfo.getId(), this);
                bukkitCloudUser.setLocale(userInfo.getLocale(), false);

                if (!event.getResult().equals(PlayerLoginEvent.Result.ALLOWED)) return;
                this.userMap.put(player.getUniqueId(), bukkitCloudUser);
                final String mode = this.plugin.getMode();
                if (mode != null) {
                    final UserModeInfoModel userModeInfoModel = quarryUserModeInfo(userInfo.getId(), mode);
                    if (userModeInfoModel == null) {
                        this.plugin.getUserModeInfoDao().create(new UserModeInfoModel(userInfo, mode));
                    }
                }
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

    @SneakyThrows
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChat(AsyncPlayerChatEvent event) {
        final UUID uuid = event.getPlayer().getUniqueId();
        final BukkitCloudUser user = this.getUser(uuid);
        PunishmentModel activeMute = user.queryUserInfo().getActiveMute();
        boolean mode = false;
        if (activeMute == null && this.plugin.getMode() != null) {
            activeMute = user.queryUserModeInfo(this.plugin.getMode()).getActiveMute();
            mode = true;
        }
        if (activeMute == null) return;
        if (activeMute.getEnd() != null && new Date().after(activeMute.getEnd())) {
            final UpdateBuilder<?, Long> updateBuilder;
            if (mode) {
                updateBuilder = getPlugin().getUserModeInfoDao().updateBuilder();
            } else {
                updateBuilder = getPlugin().getUserInfoDao().updateBuilder();
            }
            updateBuilder.updateColumnValue("activeMute_id", null);
            updateBuilder.where().idEq(user.getDatabaseId());
            updateBuilder.update();
            return;
        }
        event.setCancelled(true);
        user.sendMessage(MessageType.ERROR, CoreMessageKeys.MUTED, "{0}", activeMute.getReason(), "{1}",
                Util.formatDate(activeMute.getEnd(), user.getLocale()));
    }

    @Override
    public BukkitCloudUser getUser(UUID uuid) {
        return this.userMap.get(uuid);
    }
}
