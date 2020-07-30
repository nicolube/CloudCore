package de.lightfall.core.bungee.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.MessageKeys;
import co.aikar.commands.annotation.*;
import de.dytanic.cloudnet.CloudNet;
import de.dytanic.cloudnet.ext.bridge.player.IPlayerManager;
import de.lightfall.core.api.CoreAPI;
import de.lightfall.core.api.message.CoreMessageKeys;
import de.lightfall.core.bungee.usermanager.BungeeCloudUser;

@CommandAlias("unmute")
@CommandPermission("core.punish.unmute")
public class UnmuteCommand extends BaseCommand {

    @Default
    @Syntax("{@@core.cmd_unmute_syntax}")
    @Description("{@@core.cmd_unmute_description}")
    @CommandCompletion("@cloudPlayers @nothing")
    public void onUnmute(BungeeCloudUser sender, @Single String offlinePlayerName, @Optional String comment) {
        CommandIssuer issuer = getCurrentCommandIssuer();
        IPlayerManager playerManager = CloudNet.getInstance().getServicesRegistry().getFirstService(IPlayerManager.class);
        playerManager.getOfflinePlayersAsync(offlinePlayerName).onComplete((listITask, iCloudOfflinePlayers) -> {
            if (iCloudOfflinePlayers.isEmpty()) {
                issuer.sendError(MessageKeys.COULD_NOT_FIND_PLAYER, "{search}", offlinePlayerName);
                return;
            }
            CoreAPI.getInstance().getUserManager().loadUser(iCloudOfflinePlayers.get(0).getUniqueId())
                    .thenAccept(offlineCloudPlayer -> offlineCloudPlayer.unMute(sender, null, comment).thenAccept(unbaned -> {
                if (unbaned) {
                    issuer.sendInfo(CoreMessageKeys.CMD_UNMUTE_UNMUTED, "{0}", iCloudOfflinePlayers.get(0).getName());
                    return;
                }
                issuer.sendInfo(CoreMessageKeys.CMD_UNMUTE_NOT_MUTED);
            }));
        });
    }
}
