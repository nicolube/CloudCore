package de.lightfall.core.bukkit;

import de.lightfall.core.api.channelhandeler.ChannelHandler;
import de.lightfall.core.api.channelhandeler.documents.Document;
import de.lightfall.core.api.channelhandeler.documents.TeleportDocument;
import de.lightfall.core.api.channelhandeler.documents.TeleportType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class BukkitChannelHandler extends ChannelHandler {
    @Override
    public void receive(Document document) {
        if (document instanceof TeleportDocument) {
            Player player = Bukkit.getPlayer(((TeleportDocument) document).getUuid());
            if (player.isOnline()) {
                if (((TeleportDocument) document).getTeleportType().equals(TeleportType.PLAYER))
                    player.teleport(Bukkit.getPlayer(((TeleportDocument) document).getTargetUuid()));
                player.teleport(BukkitUtil.DocumentToLocation(((TeleportDocument) document).getTargetPosition()));
            }
        }
    }
}
