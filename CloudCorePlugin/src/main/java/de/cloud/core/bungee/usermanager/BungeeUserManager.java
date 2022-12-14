package de.cloud.core.bungee.usermanager;

import com.j256.ormlite.stmt.UpdateBuilder;
import de.cloud.core.api.Util;
import de.cloud.core.bungee.MainBungee;
import de.cloud.core.common.models.PunishmentModel;
import de.cloud.core.common.models.UserInfoModel;
import de.cloud.core.utils.usermanager.UserManager;
import lombok.Getter;
import lombok.SneakyThrows;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class BungeeUserManager extends UserManager implements Listener {

    @Getter
    private final MainBungee plugin;

    private final Map<UUID, BungeeCloudUser> userMap;

    public BungeeUserManager(MainBungee plugin) {
        super(plugin);
        this.plugin = plugin;
        this.userMap = new HashMap<>();
        onLoad();
        plugin.getProxy().getScheduler().schedule(plugin, () -> CompletableFuture.runAsync(() -> {
            try {
                Long[] ids = userMap.values().stream().map(BungeeCloudUser::getDatabaseId).toArray(Long[]::new);
                if (ids.length == 0) return;
                final UpdateBuilder<UserInfoModel, Long> ontime = this.plugin.getDatabaseProvider().getUserInfoDao().updateBuilder()
                        .updateColumnExpression("ontime", "ontime+60");
                ontime.where().in("id", ids);
                ontime.update();
            } catch (SQLException throwable) {
                throwable.printStackTrace();
            }
        }), 1, 1, TimeUnit.MINUTES);
    }

    public void onLoad() {
        this.userMap.clear();
        this.plugin.getProxy().getPlayers().forEach(p -> {
            final UUID uuid = p.getUniqueId();
            final UserInfoModel userInfoModel = quarryUserInfo(uuid);
            final BungeeCloudUser bungeeCloudUser = new BungeeCloudUser(uuid, userInfoModel.getId(), this);
            bungeeCloudUser.setPlayer(p);
            bungeeCloudUser.setLocale(userInfoModel.getLocale(), false);
            this.userMap.put(uuid, bungeeCloudUser);
        });
    }

    @SneakyThrows
    @EventHandler
    public void onLogin(LoginEvent event) {
        PendingConnection connection = event.getConnection();
        final UUID uuid = connection.getUniqueId();
        String name = connection.getName();
        UserInfoModel userInfoModel = quarryUserInfo(uuid);
        if (userInfoModel == null)
            userInfoModel = this.plugin.getDatabaseProvider().getUserInfoDao().createIfNotExists(new UserInfoModel(uuid, name));
        else if (!userInfoModel.getName().equals(name)){
            userInfoModel.setName(name);
            this.plugin.getDatabaseProvider().getUserInfoDao().updateBuilder().updateColumnExpression("name", name).where().idEq(userInfoModel.getId()).query();
        }
        final BungeeCloudUser bungeeCloudUser = new BungeeCloudUser(uuid, userInfoModel.getId(), this);
        Locale locale = Locale.forLanguageTag(userInfoModel.getLocale());
        final PunishmentModel activeBan = userInfoModel.getActiveBan();
        if (activeBan != null) {
            if (activeBan.getEnd() != null && activeBan.getEnd().before(new Date())) {
                userInfoModel.setActiveBan(null);
                this.plugin.getDatabaseProvider().getUserInfoDao().update(userInfoModel);
            } else {
                event.setCancelled(true);
                event.setCancelReason(new TextComponent((Util.formatBan(activeBan.getEnd(), activeBan.getReason(), locale))));
            }
        }
        if (event.isCancelled()) return;
        this.userMap.put(uuid, bungeeCloudUser);
    }

    @EventHandler
    public void onPostLogin(PostLoginEvent event) {
        final ProxiedPlayer player = event.getPlayer();
        final BungeeCloudUser bungeeCloudUser = this.userMap.get(player.getUniqueId());
        bungeeCloudUser.setPlayer(player);
        String locale = bungeeCloudUser.queryUserInfo().getLocale();
        if (locale != null) {
            bungeeCloudUser.setLocale(locale, false);
        } else
            bungeeCloudUser.setLocale(player.getLocale(), true);
        bungeeCloudUser.setRealName(player.getName());
    }

    @EventHandler
    public void onDisconnect(PlayerDisconnectEvent event) {
        this.userMap.remove(event.getPlayer().getUniqueId());
    }

    @Override
    public BungeeCloudUser getUser(UUID uuid) {
        return this.userMap.get(uuid);
    }
}