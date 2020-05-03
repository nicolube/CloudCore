package de.lightfall.core.bungee.usermanager;

import de.lightfall.core.InternalCoreAPI;
import de.lightfall.core.api.usermanager.ICloudUser;
import de.lightfall.core.api.usermanager.IUserManager;
import de.lightfall.core.bungee.MainBungee;
import de.lightfall.core.models.UserInfoModel;
import de.lightfall.core.usermanager.UserManager;
import lombok.Getter;
import lombok.SneakyThrows;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.lang.reflect.Array;
import java.sql.SQLException;
import java.util.*;

public class BungeeUserManager extends UserManager implements Listener {

    @Getter
    private MainBungee plugin;

    private Map<UUID, BungeeCloudUser> userMap;

    public BungeeUserManager(MainBungee plugin) {
        this.plugin = plugin;
        this.userMap = new HashMap<>();
        onLoad();
    }

    public void onLoad() {
        this.userMap.clear();
        this.plugin.getProxy().getPlayers().forEach(p -> {
            final UUID uuid = p.getUniqueId();
            final UserInfoModel userInfoModel = quarryUserInfo(uuid);
            final BungeeCloudUser bungeeCloudUser = new BungeeCloudUser(uuid, userInfoModel.getId());
            bungeeCloudUser.setPlayer(p);
            this.userMap.put(uuid, bungeeCloudUser);
        });
    }

    @EventHandler
    public void onLogin(LoginEvent event) {
        final UUID uuid = event.getConnection().getUniqueId();
        try {
            this.plugin.getPlayerDao().create(new UserInfoModel(uuid));
        } catch (SQLException ex) {};
        final UserInfoModel userInfoModel = quarryUserInfo(uuid);
        final BungeeCloudUser bungeeCloudUser = new BungeeCloudUser(uuid, userInfoModel.getId());
        // Todo ban cancel / whitelist
        if (event.isCancelled()) return;
        this.userMap.put(uuid, bungeeCloudUser);
    }

    @EventHandler
    public void onPostLogin(PostLoginEvent event) {
        final ProxiedPlayer player = event.getPlayer();
        event.getPlayer().getLocale();
        this.userMap.get(player.getUniqueId()).setPlayer(player);
    }

    @EventHandler
    public void onDisconnect(PlayerDisconnectEvent event) {
        this.userMap.remove(event.getPlayer().getUniqueId());
    }

    @Override
    public ICloudUser getUser(UUID uuid) {
        final BungeeCloudUser bungeeCloudUser = this.userMap.get(uuid);
        if (bungeeCloudUser != null)
            return bungeeCloudUser;
        // Todo offline load
        throw new NotImplementedException();
    }
}
