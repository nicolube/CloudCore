package de.cloud.core.bungee.usermanager;

import co.aikar.commands.MessageType;
import de.cloud.core.api.message.IMessageKeyProvider;
import de.cloud.core.bungee.MainBungee;
import de.cloud.core.utils.usermanager.CloudUser;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.UUID;

public class BungeeCloudUser extends CloudUser {

    @Getter @Setter(value = AccessLevel.PROTECTED)
    private ProxiedPlayer player;
    private transient MainBungee plugin = MainBungee.getInstance();

    public BungeeCloudUser(UUID uuid, long databaseId, BungeeUserManager userManager) {
        super(uuid, null, databaseId, userManager);
    }

    @Override
    public void sendMessage(MessageType type, IMessageKeyProvider key, String... replacements) {
        if (isOnline()) {
            this.plugin.getCommandManager().getCommandIssuer(player).sendMessage(type, key, replacements);
            return;
        }
        super.sendMessage(type, key, replacements);
    }

    @Override
    public boolean isOnline() {
        return this.player != null && this.player.isConnected();
    }

    protected void setRealName(String name) {
        super.realName = name;
    }

    @Override
    public String getName() {
        return this.player.getName();
    }


}
