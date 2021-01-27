package de.cloud.core.bungee.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.MessageKeys;
import co.aikar.commands.annotation.*;
import de.cloud.core.api.Util;
import de.cloud.core.api.message.CoreMessageKeys;
import de.cloud.core.bungee.MainBungee;
import de.cloud.core.bungee.usermanager.BungeeCloudUser;
import de.dytanic.cloudnet.CloudNet;
import de.dytanic.cloudnet.ext.bridge.player.IPlayerManager;

import java.util.Date;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@CommandAlias("tempmute")
@CommandPermission("core.punish.tempmute")
public class TempmuteCommand extends BaseCommand {

    private final MainBungee plugin;

    public TempmuteCommand(MainBungee plugin) {
        this.plugin = plugin;
    }

    @Default
    @CommandCompletion("@cloudPlayers @nothing")
    @Description("{@@core.cmd_tempmute_description}")
    @Syntax("{@@core.cmd_tempmute_syntax}")
    public void onMute(BungeeCloudUser sender, @Single String offlinePlayerName, @Single String time, @Optional String reason) {
        CommandIssuer issuer = getCurrentCommandIssuer();
        Long timeInSeconds = Util.stringToMilesPhrase(time);

        if (timeInSeconds == null) {
            issuer.sendError(CoreMessageKeys.TIMEFORMAT_FAIL);
            return;
        }
        IPlayerManager playerManager = CloudNet.getInstance().getServicesRegistry().getFirstService(IPlayerManager.class);
        playerManager.getOfflinePlayersAsync(offlinePlayerName).onComplete((listITask, iCloudOfflinePlayers) -> {
            String lReason;
            if (reason == null)
                lReason = "Kein Grund angegeben / No reason given";
            else
                lReason = reason;
            if (iCloudOfflinePlayers.isEmpty()) {
                issuer.sendError(MessageKeys.COULD_NOT_FIND_PLAYER, "{search}", offlinePlayerName);
                return;
            }
            final UUID uniqueId = iCloudOfflinePlayers.get(0).getUniqueId();
            this.plugin.getUserManager().loadUser(uniqueId).thenAcceptAsync(offlineCloudUser -> {
                try {
                    if (sender.getWight().get() <= offlineCloudUser.getWight().get()) {
                        issuer.sendInfo(CoreMessageKeys.CMD_MUTE_LOWER_RANK);
                        return;
                    }
                    issuer.sendInfo(CoreMessageKeys.CMD_TEMPMUTE_MUTED, "{0}", iCloudOfflinePlayers.get(0).getName(),
                            "{1}", Util.formatDate(new Date(TimeUnit.SECONDS.toMillis(timeInSeconds) + System.currentTimeMillis()), sender.getLocale()),
                            "{2}", lReason);
                    offlineCloudUser.tempMute(sender, null, timeInSeconds, lReason);
                } catch (ExecutionException | InterruptedException ex) {
                    ex.printStackTrace();
                }
            });
        });
    }


}
