package de.lightfall.core.bungee.usermanager;

import com.j256.ormlite.stmt.UpdateBuilder;
import de.lightfall.core.api.Util;
import de.lightfall.core.bungee.MainBungee;
import de.lightfall.core.models.PunishmentModel;
import de.lightfall.core.models.UserInfoModel;
import de.lightfall.core.usermanager.UserManager;
import lombok.Getter;
import lombok.SneakyThrows;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class BungeeUserManager extends UserManager implements Listener {

    @Getter
    private MainBungee plugin;

    private Map<UUID, BungeeCloudUser> userMap;

    public BungeeUserManager(MainBungee plugin) {
        this.plugin = plugin;
        this.userMap = new HashMap<>();
        onLoad();
        plugin.getProxy().getScheduler().schedule(plugin, () -> {
            CompletableFuture.runAsync(() -> {
                try {
                    Long[] ids = userMap.values().stream().map(BungeeCloudUser::getDatabaseId).toArray(Long[]::new);
                    if (ids.length == 0) return;
                    final UpdateBuilder<UserInfoModel, Long> ontime = this.plugin.getPlayerDao().updateBuilder()
                            .updateColumnExpression("ontime", "ontime+60");
                    ontime.where().in("id", ids);
                    ontime.update();
                } catch (SQLException throwable) {
                    throwable.printStackTrace();
                }
            });
        }, 1, 1, TimeUnit.MINUTES);
    }

    public void onLoad() {
        this.userMap.clear();
        this.plugin.getProxy().getPlayers().forEach(p -> {
            final UUID uuid = p.getUniqueId();
            final UserInfoModel userInfoModel = quarryUserInfo(uuid);
            final BungeeCloudUser bungeeCloudUser = new BungeeCloudUser(uuid, userInfoModel.getId(), this);
            bungeeCloudUser.setLocale(userInfoModel.getLocale());
            bungeeCloudUser.setPlayer(p);
            this.userMap.put(uuid, bungeeCloudUser);
        });
    }

    @SneakyThrows
    @EventHandler
    public void onLogin(LoginEvent event) {
        final UUID uuid = event.getConnection().getUniqueId();
        try {
            this.plugin.getPlayerDao().create(new UserInfoModel(uuid));
        } catch (SQLException ex) {
        }
        ;
        final UserInfoModel userInfoModel = quarryUserInfo(uuid);
        final BungeeCloudUser bungeeCloudUser = new BungeeCloudUser(uuid, userInfoModel.getId(), this);
        bungeeCloudUser.setLocale(userInfoModel.getLocale());
        // Todo whitelist
        Locale locale = Locale.forLanguageTag(userInfoModel.getLocale());
        final PunishmentModel activeBan = userInfoModel.getActiveBan();
        if (activeBan != null) {
            if (activeBan.getEnd().before(new Date())) {
                userInfoModel.setActiveBan(null);
                this.plugin.getPlayerDao().update(userInfoModel);
            } else {
                event.setCancelled(true);
                System.out.println(activeBan.getEnd());
                event.setCancelReason(new TextComponent((Util.formatBan(activeBan.getEnd(), activeBan.getReason(), locale))));
            }
        }
        if (event.isCancelled()) return;
        this.userMap.put(uuid, bungeeCloudUser);
    }

    @EventHandler
    public void onPostLogin(PostLoginEvent event) {
        final ProxiedPlayer player = event.getPlayer();
        event.getPlayer().getLocale();
        final BungeeCloudUser bungeeCloudUser = this.userMap.get(player.getUniqueId());
        bungeeCloudUser.setPlayer(player);
        bungeeCloudUser.setRealName(player.getName());
    }

    @EventHandler
    public void onDisconnect(PlayerDisconnectEvent event) {
        this.userMap.remove(event.getPlayer().getUniqueId());
    }

    @Override
    public BungeeCloudUser getUser(UUID uuid) {
        final BungeeCloudUser bungeeCloudUser = this.userMap.get(uuid);
        if (bungeeCloudUser != null)
            return bungeeCloudUser;
        // Todo offline load
        throw new NotImplementedException();
    }
}