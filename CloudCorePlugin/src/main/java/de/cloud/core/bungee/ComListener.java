package de.cloud.core.bungee;

import com.j256.ormlite.stmt.UpdateBuilder;
import de.cloud.core.bungee.usermanager.BungeeCloudUser;
import de.cloud.core.com.client.MinecraftListener;
import de.cloud.core.common.LinkStatus;
import de.cloud.core.common.models.UserInfoModel;
import de.cloud.core.common.packet.PacketOutRequestLink;
import lombok.SneakyThrows;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.net.InetSocketAddress;

class ComListener implements MinecraftListener {

    private MainBungee plugin;

    public ComListener(MainBungee plugin) {
        this.plugin = plugin;
    }

    @Override
    @SneakyThrows
    public void onLinkRequest(PacketOutRequestLink packet) {
        ProxiedPlayer player = plugin.getProxy().getPlayer(packet.getUsername());
        if (player == null) plugin.getComClient().confirmLink(packet.getDbId(), -1, LinkStatus.NOT_ONLINE);
        String address = ((InetSocketAddress) player.getSocketAddress()).getAddress().getHostAddress();
        if (address.equals(packet.getIp())) {
            BungeeCloudUser user = plugin.getUserManager().getUser(player.getUniqueId());
            UpdateBuilder<UserInfoModel, Long> updateBuilder = plugin.getDatabaseProvider().getUserInfoDao().updateBuilder();
            updateBuilder.where().idEq(user.getDatabaseId());
            updateBuilder.updateColumnValue("teamspeak_id", packet.getDbId());
            updateBuilder.update();
            plugin.getComClient().confirmLink(packet.getDbId(), user.databaseId, LinkStatus.SUCCESS);
            return;
        }
    }
}