package de.lightfall.core.bungee.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.MessageKeys;
import co.aikar.commands.annotation.*;
import co.aikar.commands.bungee.contexts.OnlinePlayer;
import de.dytanic.cloudnet.ext.bridge.BridgePlayerManager;
import de.lightfall.core.api.message.CoreMessageKeys;
import de.lightfall.core.api.usermanager.ICloudUser;
import de.lightfall.core.bungee.MainBungee;

import java.util.UUID;

@CommandAlias("ban")
@CommandPermission("system.punishments.command.ban")
public class BanCommand extends BaseCommand {

    private MainBungee plugin;

    public BanCommand(MainBungee mainBungee) {
        this.plugin = mainBungee;
    }

    @Default
    @Description("@@core.cmd_permban_description")
    @Syntax("@@core.cmd_permban_syntax")
    @CommandCompletion("@cloudPlayers")
    public void onBan(ICloudUser sender, OnlinePlayer onlinePlayer) {
        final UUID uniqueId = onlinePlayer.getPlayer().getUniqueId();
        this.plugin.getUserManager().loadUser(uniqueId).thenAccept(offlineCloudUser -> {
            offlineCloudUser.ban(sender, null, "Kein Grund angegeben /  No reason given");
            getCurrentCommandIssuer().sendInfo(CoreMessageKeys.BANNED_PLAYER_PERMANENTLY,"{0}",onlinePlayer.getPlayer().getName(),"{1}","Kein Grund angegeben / No reason given");
        });
    }

    @Default
    @Description("@@core.cmd_permban_description")
    @Syntax("@@core.cmd_permban_syntax")
    @CommandCompletion("@cloudPlayers")
    public void onBan(ICloudUser sender, OnlinePlayer onlinePlayer,String reason) {
        final UUID uniqueId = onlinePlayer.getPlayer().getUniqueId();
        this.plugin.getUserManager().loadUser(uniqueId).thenAccept(offlineCloudUser -> {
            offlineCloudUser.ban(sender, null, reason);
            getCurrentCommandIssuer().sendInfo(CoreMessageKeys.BANNED_PLAYER_PERMANENTLY,"{0}",onlinePlayer.getPlayer().getName(),"{1}",reason);
        });
    }

    @Default
    @Description("@@core.cmd_permban_description")
    @Syntax("@@core.cmd_permban_syntax")
    @CommandCompletion("@cloudPlayers")
    public void onBan(ICloudUser sender, String player) {
        BridgePlayerManager.getInstance().getOfflinePlayerAsync(player).onComplete((listITask, iCloudOfflinePlayers) -> {
            if (iCloudOfflinePlayers.isEmpty()) {
                getCurrentCommandIssuer().sendError(MessageKeys.COULD_NOT_FIND_PLAYER);
                return;
            }
            final UUID uniqueId = iCloudOfflinePlayers.get(0).getUniqueId();
            this.plugin.getUserManager().loadUser(uniqueId).thenAccept(offlineCloudUser -> {
                offlineCloudUser.ban(sender, null, "Kein Grund angegeben / No reason given");
                getCurrentCommandIssuer().sendInfo(CoreMessageKeys.BANNED_PLAYER_PERMANENTLY,"{0}",player,"{1}","Kein Grund angegeben / No reason given");
            });
        });
    }

    @Default
    @Description("@@core.cmd_permban_description")
    @Syntax("@@core.cmd_permban_syntax")
    @CommandCompletion("@cloudPlayers")
    public void onBan(ICloudUser sender, String player, String reason) {
        BridgePlayerManager.getInstance().getOfflinePlayerAsync(player).onComplete((listITask, iCloudOfflinePlayers) -> {
            if (iCloudOfflinePlayers.isEmpty()) {
                getCurrentCommandIssuer().sendError(MessageKeys.COULD_NOT_FIND_PLAYER);
                return;
            }
            final UUID uniqueId = iCloudOfflinePlayers.get(0).getUniqueId();
            this.plugin.getUserManager().loadUser(uniqueId).thenAccept(offlineCloudUser -> {
                offlineCloudUser.ban(sender, null, reason);
                getCurrentCommandIssuer().sendInfo(CoreMessageKeys.BANNED_PLAYER_PERMANENTLY,"{0}",player,"{1}",reason);
            });
        });
    }

}
