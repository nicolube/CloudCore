package de.lightfall.core.bungee.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.MessageKeys;
import co.aikar.commands.annotation.*;
import de.dytanic.cloudnet.ext.bridge.BridgePlayerManager;
import de.lightfall.core.api.CoreAPI;
import de.lightfall.core.api.message.CoreMessageKeys;
import de.lightfall.core.api.usermanager.ICloudUser;
import de.lightfall.core.bungee.usermanager.BungeeCloudUser;

@CommandAlias("unmute")
@CommandPermission("system.punishments.command.unmute")
public class UnmuteCommand extends BaseCommand {

    @Default
    @Syntax("{@@core.cmd_unmute_syntax}")
    @Description("{@@core.cmd_unmute_description}")
    @CommandCompletion("@cloudPlayers @nothing")
    public void onUnmute(BungeeCloudUser sender, String offlineCloudUser) {
        CommandIssuer issuer = getCurrentCommandIssuer();

        BridgePlayerManager.getInstance().getOfflinePlayerAsync(offlineCloudUser).onComplete((listITask, iCloudOfflinePlayers) -> {
            if (iCloudOfflinePlayers.isEmpty()) {
                issuer.sendError(MessageKeys.COULD_NOT_FIND_PLAYER);
                return;
            }
            CoreAPI.getInstance().getUserManager().loadUser(iCloudOfflinePlayers.get(0).getUniqueId()).thenAccept(offlineCloudPlayer -> {
                offlineCloudPlayer.unMute(sender, null, "Entfällt").thenAccept(unbaned -> {
                    if (unbaned) {
                        issuer.sendInfo(CoreMessageKeys.PLAYER_UNMUTED, "{0}", iCloudOfflinePlayers.get(0).getName());
                        return;
                    }
                    issuer.sendInfo(CoreMessageKeys.PLAYER_NOT_MUTED);
                });
            });
        });
    }
}
