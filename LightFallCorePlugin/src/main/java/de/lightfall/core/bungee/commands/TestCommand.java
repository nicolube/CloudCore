package de.lightfall.core.bungee.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.bungee.contexts.OnlinePlayer;
import de.dytanic.cloudnet.driver.CloudNetDriver;
import de.dytanic.cloudnet.ext.bridge.BridgePlayerManager;
import de.lightfall.core.api.CoreAPI;
import de.lightfall.core.api.channelhandeler.ChannelHandler;
import de.lightfall.core.api.channelhandeler.documents.ReloadDocument;
import de.lightfall.core.api.channelhandeler.documents.ReloadType;
import de.lightfall.core.api.message.CoreMessageKeys;
import de.lightfall.core.bungee.MainBungee;
import de.lightfall.core.bungee.usermanager.BungeeCloudUser;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.UUID;

@CommandAlias("test")
@CommandPermission("core.test")
public class TestCommand extends BaseCommand {
    private final MainBungee plugin;

    public TestCommand(MainBungee plugin) {
        this.plugin = plugin;
    }

    @Subcommand("teleport")
    @CommandCompletion("@players")
    public void onTeleport(ProxiedPlayer player, OnlinePlayer target) {
        CoreAPI.getInstance().getUserManager().getUser(player.getUniqueId()).moveToPlayerTeleport(target.getPlayer().getUniqueId());
    }

    @Subcommand("messages")
    public void onMessages() {
        getCurrentCommandIssuer().sendInfo(CoreMessageKeys.CMD_KILL_TASK_NO_GROUP);
        getCurrentCommandIssuer().sendInfo(CoreMessageKeys.CMD_KILL_TASK_STOPPED);
    }

    @Subcommand("ban")
    @CommandCompletion("@players")
    public void onBan(BungeeCloudUser sender, String username, String reason) {
        BridgePlayerManager.getInstance().getOfflinePlayerAsync(username).onComplete((listITask, iCloudOfflinePlayers) -> {
            final UUID uniqueId = iCloudOfflinePlayers.get(0).getUniqueId();
            this.plugin.getUserManager().loadUser(uniqueId).thenAccept(offlineCloudUser -> {
                offlineCloudUser.ban(sender, null, reason);
            });
        });
    }

    @Subcommand("unban")
    @CommandCompletion("@players")
    public void onUnBan(BungeeCloudUser sender, String username, String reason) {
        BridgePlayerManager.getInstance().getOfflinePlayerAsync(username).onComplete((listITask, iCloudOfflinePlayers) -> {
            final UUID uniqueId = iCloudOfflinePlayers.get(0).getUniqueId();
            this.plugin.getUserManager().loadUser(uniqueId).thenAccept(offlineCloudUser -> {
                offlineCloudUser.unBan(sender, null, reason);
            });
        });
    }

    @Subcommand("reload messages")
    public void onReloadMessage(CommandSender sender) {
        ChannelHandler.send(new ReloadDocument(ReloadType.MESSAGES));
        sender.sendMessage(TextComponent.fromLegacyText("Reloaded"));
    }
}
