package de.cloud.core.bungee.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.MessageKeys;
import co.aikar.commands.annotation.*;
import de.cloud.core.api.CoreAPI;
import de.cloud.core.api.message.CoreMessageKeys;
import de.cloud.core.bungee.usermanager.BungeeCloudUser;
import de.dytanic.cloudnet.CloudNet;
import de.dytanic.cloudnet.driver.CloudNetDriver;
import de.dytanic.cloudnet.ext.bridge.player.IPlayerManager;

@CommandAlias("unban")
@CommandPermission("core.punish.unmban")
public class UnbanCommand extends BaseCommand {

    @Default
    @Syntax("{@@core.cmd_unban_syntax}")
    @Description("{@@core.cmd_unban_description}")
    public void onUnban(BungeeCloudUser sender, @Single String offlinePlayerName, @Optional String comment) {
        CommandIssuer issuer = getCurrentCommandIssuer();
        IPlayerManager playerManager = CloudNetDriver.getInstance().getServicesRegistry().getFirstService(IPlayerManager.class);
        playerManager.getOfflinePlayersAsync(offlinePlayerName).onComplete((listITask, iCloudOfflinePlayers) -> {
            if (iCloudOfflinePlayers.isEmpty()) {
                issuer.sendError(MessageKeys.COULD_NOT_FIND_PLAYER, "{search}", offlinePlayerName);
                return;
            }
            CoreAPI.getInstance().getUserManager().loadUser(iCloudOfflinePlayers.get(0).getUniqueId())
                    .thenAccept(offlineCloudPlayer -> offlineCloudPlayer.unBan(sender, null, comment).thenAccept(unbaned -> {
                if (unbaned) {
                    issuer.sendInfo(CoreMessageKeys.CMD_UNBAN_UNBANNED, "{0}", iCloudOfflinePlayers.get(0).getName());
                    return;
                }
                issuer.sendInfo(CoreMessageKeys.CMD_UNBAN_NOT_BANNED);
            }));
        });
    }

}
