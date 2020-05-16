package de.lightfall.core.bungee.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.MessageKeys;
import co.aikar.commands.annotation.*;
import de.dytanic.cloudnet.ext.bridge.BridgePlayerManager;
import de.lightfall.core.api.CoreAPI;
import de.lightfall.core.api.message.CoreMessageKeys;
import de.lightfall.core.bungee.usermanager.BungeeCloudUser;

@CommandAlias("unban")
@CommandPermission("core.punish.unmban")
public class UnbanCommand extends BaseCommand {

    @Default
    @Syntax("{@@core.cmd_unban_syntax}")
    @Description("{@@core.cmd_unban_description}")
    public void onUnban(BungeeCloudUser sender, @Single String offlinePlayerName, @Optional String comment) {
        CommandIssuer issuer = getCurrentCommandIssuer();
        BridgePlayerManager.getInstance().getOfflinePlayerAsync(offlinePlayerName).onComplete((listITask, iCloudOfflinePlayers) -> {
            if (iCloudOfflinePlayers.isEmpty()) {
                issuer.sendError(MessageKeys.COULD_NOT_FIND_PLAYER, "{search}", offlinePlayerName);
                return;
            }
            CoreAPI.getInstance().getUserManager().loadUser(iCloudOfflinePlayers.get(0).getUniqueId()).thenAccept(offlineCloudPlayer -> {
                offlineCloudPlayer.unBan(sender, null, comment).thenAccept(unbaned -> {
                    if (unbaned) {
                        issuer.sendInfo(CoreMessageKeys.CMD_UNBAN_UNBANNED, "{0}", iCloudOfflinePlayers.get(0).getName());
                        return;
                    }
                    issuer.sendInfo(CoreMessageKeys.CMD_UNBAN_NOT_BANNED);
                });
            });
        });
    }

}
