package de.lightfall.core.bukkit;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.cacheddata.CachedMetaData;
import net.luckperms.api.context.ContextManager;
import net.luckperms.api.model.user.User;
import net.luckperms.api.query.QueryOptions;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.UUID;

public class ChatManager implements Listener {

    private final LuckPerms luckPerms;
    private final ContextManager contextManager;

    public ChatManager(LuckPerms luckPerms) {
        this.luckPerms = luckPerms;
        this.contextManager = luckPerms.getContextManager();
    }

    @EventHandler
    public void onChatEvent(AsyncPlayerChatEvent event) {
        final Player player = event.getPlayer();
        if (player.hasPermission("lightfall.chat.color")) {
            event.setMessage(ChatColor.translateAlternateColorCodes('&', event.getMessage()));
        }
        final CachedMetaData userMeta = getUserMeta(event.getPlayer().getUniqueId());
        final String format = ChatColor.translateAlternateColorCodes('&', String.format(userMeta.getMetaValue("chat_format"),
                player.getWorld().getName(), userMeta.getPrefix(), userMeta.getSuffix(), userMeta.getMetaValue("chat_color")));
        event.setFormat(format.replace("{u}", "%1$s").replace("{m}", "%2$s"));
    }

    private CachedMetaData getUserMeta(UUID uuid) {
        final User user = this.luckPerms.getUserManager().getUser(uuid);
        return user.getCachedData().getMetaData(QueryOptions.contextual(this.contextManager.getContext(user)
                .orElseGet(contextManager::getStaticContext)));
    }
}
