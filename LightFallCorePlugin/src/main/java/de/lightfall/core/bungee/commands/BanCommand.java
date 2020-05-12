package de.lightfall.core.bungee.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.MessageKeys;
import co.aikar.commands.annotation.*;
import co.aikar.commands.bungee.contexts.OnlinePlayer;
import de.dytanic.cloudnet.ext.bridge.BridgePlayerManager;
import de.lightfall.core.api.message.CoreMessageKeys;
import de.lightfall.core.api.usermanager.ICloudUser;
import de.lightfall.core.bungee.MainBungee;
import de.lightfall.core.bungee.usermanager.BungeeCloudUser;

import java.util.UUID;

@CommandAlias("ban")
@CommandPermission("system.punishments.command.ban")
public class BanCommand extends BaseCommand {

    private MainBungee plugin;

    public BanCommand(MainBungee plugin) {
        this.plugin = plugin;
    }

    @Default
    @Description("{@@core.cmd_ban_description}")
    @Syntax("{@@core.cmd_ban_syntax}")
    @CommandCompletion("@cloudPlayers @nothing")
    @CommandPermission("system.punishments.noreason")
    public void onBan(BungeeCloudUser sender, OnlinePlayer onlinePlayer) {
        CommandIssuer issuer = getCurrentCommandIssuer();
        final UUID uniqueId = onlinePlayer.getPlayer().getUniqueId();
        this.plugin.getUserManager().loadUser(uniqueId).thenAccept(offlineCloudUser -> {
            offlineCloudUser.ban(sender, null, "Kein Grund angegeben /  No reason given");
            issuer.sendInfo(CoreMessageKeys.BANNED_PLAYER_PERMANENTLY, "{0}", onlinePlayer.getPlayer().getName(), "{1}", "Kein Grund angegeben / No reason given");
        });
    }

    @Default
    @Description("{@@core.cmd_ban_description}")
    @Syntax("{@@core.cmd_ban_syntax}")
    @CommandCompletion("@cloudPlayers @nothing")
    public void onBan(BungeeCloudUser sender, OnlinePlayer onlinePlayer, String reason) {
        CommandIssuer issuer = getCurrentCommandIssuer();
        final UUID uniqueId = onlinePlayer.getPlayer().getUniqueId();
        this.plugin.getUserManager().loadUser(uniqueId).thenAccept(offlineCloudUser -> {
            offlineCloudUser.ban(sender, null, reason);
            issuer.sendInfo(CoreMessageKeys.BANNED_PLAYER_PERMANENTLY, "{0}", onlinePlayer.getPlayer().getName(), "{1}", reason);
        });
    }

    @Default
    @Description("{@@core.cmd_ban_description}")
    @Syntax("{@@core.cmd_ban_syntax}")
    @CommandPermission("system.punishments.noreason")
    @CommandCompletion("@cloudPlayers @nothing")
    public void onBan(BungeeCloudUser sender, String player) {
        CommandIssuer issuer = getCurrentCommandIssuer();
        BridgePlayerManager.getInstance().getOfflinePlayerAsync(player).onComplete((listITask, iCloudOfflinePlayers) -> {
            if (iCloudOfflinePlayers.isEmpty()) {
                issuer.sendError(MessageKeys.COULD_NOT_FIND_PLAYER);
                return;
            }
            final UUID uniqueId = iCloudOfflinePlayers.get(0).getUniqueId();
            this.plugin.getUserManager().loadUser(uniqueId).thenAccept(offlineCloudUser -> {
                offlineCloudUser.ban(sender, null, "Kein Grund angegeben / No reason given");
                issuer.sendInfo(CoreMessageKeys.BANNED_PLAYER_PERMANENTLY, "{0}", iCloudOfflinePlayers.get(0).getName(), "{1}", "Kein Grund angegeben / No reason given");
            });
        });
    }

    @Default
    @Description("{@@core.cmd_ban_description}")
    @Syntax("{@@core.cmd_ban_syntax}")
    @CommandCompletion("@cloudPlayers @nothing")
    public void onBan(BungeeCloudUser sender, String player, String reason) {
        CommandIssuer issuer = getCurrentCommandIssuer();
        BridgePlayerManager.getInstance().getOfflinePlayerAsync(player).onComplete((listITask, iCloudOfflinePlayers) -> {
            if (iCloudOfflinePlayers.isEmpty()) {
                issuer.sendError(MessageKeys.COULD_NOT_FIND_PLAYER);
                return;
            }
            final UUID uniqueId = iCloudOfflinePlayers.get(0).getUniqueId();
            this.plugin.getUserManager().loadUser(uniqueId).thenAccept(offlineCloudUser -> {
                offlineCloudUser.ban(sender, null, reason);
                issuer.sendInfo(CoreMessageKeys.BANNED_PLAYER_PERMANENTLY, "{0}", iCloudOfflinePlayers.get(0).getName(), "{1}", reason);
            });
        });
    }

}
