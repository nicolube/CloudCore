package de.lightfall.core.bungee.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.MessageKeys;
import co.aikar.commands.annotation.*;
import de.dytanic.cloudnet.ext.bridge.BridgePlayerManager;
import de.lightfall.core.api.CoreAPI;
import de.lightfall.core.api.message.CoreMessageKeys;
import de.lightfall.core.api.usermanager.ICloudUser;

@CommandAlias("unban")
@CommandPermission("system.punishments.command.unban")
public class UnbanCommand extends BaseCommand {

    @Default
    @Syntax("@@core.cmd_unban_syntax")
    @Description("@@core.cmd_unban_description")
    public void onUnban(ICloudUser sender, String offlineCloudUser) {

        BridgePlayerManager.getInstance().getOfflinePlayerAsync(offlineCloudUser).onComplete((listITask, iCloudOfflinePlayers) -> {
            if (iCloudOfflinePlayers.isEmpty()) {
                getCurrentCommandIssuer().sendError(MessageKeys.COULD_NOT_FIND_PLAYER);
                return;
            }
            CoreAPI.getInstance().getUserManager().loadUser(iCloudOfflinePlayers.get(0).getUniqueId()).thenAccept(offlineCloudPlayer -> {
               offlineCloudPlayer.unBan(sender,null,"Entfällt").thenAccept(unbaned -> {
                   if (unbaned) {
                       getCurrentCommandIssuer().sendInfo(CoreMessageKeys.PLAYER_UNBANNED,"{0}",iCloudOfflinePlayers.get(0).getName());
                       return;
                   }
                   getCurrentCommandIssuer().sendInfo(CoreMessageKeys.PLAYER_NOT_BANNED);
               });
            });
        });
    }

}
