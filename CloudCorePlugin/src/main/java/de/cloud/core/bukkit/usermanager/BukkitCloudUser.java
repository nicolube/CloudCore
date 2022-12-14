package de.cloud.core.bukkit.usermanager;

import co.aikar.commands.MessageType;
import de.cloud.core.api.message.IMessageKeyProvider;
import de.cloud.core.bukkit.MainBukkit;
import de.cloud.core.utils.usermanager.CloudUser;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class BukkitCloudUser extends CloudUser {

    @Getter
    private final Player player;
    private final BukkitUserManager manager;

    public BukkitCloudUser(Player player, long databaseId, BukkitUserManager userManager) {
        super(player.getUniqueId(), player.getName(), databaseId, userManager);
        this.player = player;
        this.manager = userManager;
    }

    @Override
    public void sendMessage(MessageType type, IMessageKeyProvider key, String... replacements) {
        if (isOnline()) {
            JavaPlugin.getPlugin(MainBukkit.class).getCommandManager().getCommandIssuer(player).sendMessage(type, key, replacements);
            return;
        }
        super.sendMessage(type, key, replacements);
    }

    @Override
    public boolean isOnline() {
        return this.player != null && this.player.isOnline();
    }

    @Override
    public String getName() {
        return player.getName();
    }
}
