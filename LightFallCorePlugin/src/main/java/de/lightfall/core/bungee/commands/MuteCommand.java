package de.lightfall.core.bungee.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.MessageKeys;
import co.aikar.commands.annotation.*;
import co.aikar.commands.bungee.contexts.OnlinePlayer;
import de.dytanic.cloudnet.ext.bridge.BridgePlayerManager;
import de.lightfall.core.api.message.CoreMessageKeys;
import de.lightfall.core.api.usermanager.ICloudUser;
import de.lightfall.core.bungee.MainBungee;
import de.lightfall.core.bungee.usermanager.BungeeCloudUser;

import java.util.UUID;

@CommandPermission("system.punishments.command.mute")
@CommandAlias("mute")
public class MuteCommand extends BaseCommand {

    private final MainBungee plugin;

    public MuteCommand(MainBungee plugin) {
        this.plugin = plugin;
    }

    @Default
    @Description("@@core.cmd_mute_description")
    @Syntax("@@core.cmd_mute_syntax")
    @CommandPermission("system.punishments.noreason")
    @CommandCompletion("@cloudPlayers")
    public void onMute(BungeeCloudUser sender, OnlinePlayer onlinePlayer) {
        final UUID uniqueId = onlinePlayer.getPlayer().getUniqueId();
        this.plugin.getUserManager().loadUser(uniqueId).thenAccept(offlineCloudUser -> {
            offlineCloudUser.mute(sender, null, "Kein Grund angegeben /  No reason given");
            getCurrentCommandIssuer().sendInfo(CoreMessageKeys.MUTED_PLAYER_PERMANENTLY, "{0}", onlinePlayer.getPlayer().getName(), "{1}", "Kein Grund angegeben / No reason given");
        });
    }

    @Default
    @Description("@@core.cmd_mute_description")
    @Syntax("@@core.cmd_mute_syntax")
    @CommandCompletion("@cloudPlayers")
    public void onMute(BungeeCloudUser sender, String offlinePlayer) {
        BridgePlayerManager.getInstance().getOfflinePlayerAsync(offlinePlayer).onComplete((listITask, iCloudOfflinePlayers) -> {
            if (iCloudOfflinePlayers.isEmpty()) {
                getCurrentCommandIssuer().sendError(MessageKeys.COULD_NOT_FIND_PLAYER);
                return;
            }
            final UUID uniqueId = iCloudOfflinePlayers.get(0).getUniqueId();
            this.plugin.getUserManager().loadUser(uniqueId).thenAccept(offlineCloudUser -> {
                offlineCloudUser.mute(sender, null, "Kein Grund angegeben / No reason given");
                getCurrentCommandIssuer().sendInfo(CoreMessageKeys.MUTED_PLAYER_PERMANENTLY, "{0}", iCloudOfflinePlayers.get(0).getName(), "{1}", "Kein Grund angegeben / No reason given");
            });
        });
    }

    @Default
    @Description("@@core.cmd_mute_description")
    @Syntax("@@core.cmd_mute_syntax")
    @CommandPermission("system.punishments.noreason")
    @CommandCompletion("@cloudPlayers")
    public void onMute(BungeeCloudUser sender, OnlinePlayer onlinePlayer, String reason) {
        final UUID uniqueId = onlinePlayer.getPlayer().getUniqueId();
        this.plugin.getUserManager().loadUser(uniqueId).thenAccept(offlineCloudUser -> {
            offlineCloudUser.mute(sender, null, reason);
            getCurrentCommandIssuer().sendInfo(CoreMessageKeys.MUTED_PLAYER_PERMANENTLY, "{0}", onlinePlayer.getPlayer().getName(), "{1}", reason);
        });
    }

    @Default
    @Description("@@core.cmd_mute_description")
    @Syntax("@@core.cmd_mute_syntax")
    @CommandCompletion("@cloudPlayers")
    public void onMute(BungeeCloudUser sender, String offlinePlayer, String reason) {
        BridgePlayerManager.getInstance().getOfflinePlayerAsync(offlinePlayer).onComplete((listITask, iCloudOfflinePlayers) -> {
            if (iCloudOfflinePlayers.isEmpty()) {
                getCurrentCommandIssuer().sendError(MessageKeys.COULD_NOT_FIND_PLAYER);
                return;
            }
            final UUID uniqueId = iCloudOfflinePlayers.get(0).getUniqueId();
            this.plugin.getUserManager().loadUser(uniqueId).thenAccept(offlineCloudUser -> {
                offlineCloudUser.mute(sender, null, reason);
                getCurrentCommandIssuer().sendInfo(CoreMessageKeys.MUTED_PLAYER_PERMANENTLY, "{0}", iCloudOfflinePlayers.get(0).getName(), "{1}", reason);
            });
        });
    }

}
