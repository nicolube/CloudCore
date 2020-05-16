package de.lightfall.core.bungee.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.MessageKeys;
import co.aikar.commands.annotation.*;
import de.dytanic.cloudnet.ext.bridge.BridgePlayerManager;
import de.lightfall.core.api.Util;
import de.lightfall.core.api.message.CoreMessageKeys;
import de.lightfall.core.bungee.MainBungee;
import de.lightfall.core.bungee.usermanager.BungeeCloudUser;

import java.util.Date;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@CommandAlias("tempban")
@CommandPermission("core.punish.tempban")
public class TempbanCommand extends BaseCommand {

    private final MainBungee plugin;

    public TempbanCommand(MainBungee plugin) {
        this.plugin = plugin;
    }

    @Default
    @CommandCompletion("@cloudPlayers @nothing")
    @Description("{@@core.cmd_tempban_description}")
    @Syntax("{@@core.cmd_tempban_syntax}")
    public void onBan(BungeeCloudUser sender, @Single String offlinePlayerName, @Single String time, @Optional String reason) {
        CommandIssuer issuer = getCurrentCommandIssuer();
        Long timeInSeconds = Util.stringToMilesPhrase(time);
        ;

        if (timeInSeconds == null) {
            issuer.sendError(CoreMessageKeys.TIMEFORMAT_FAIL);
            return;
        }

        BridgePlayerManager.getInstance().getOfflinePlayerAsync(offlinePlayerName).onComplete((listITask, iCloudOfflinePlayers) -> {
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
                        issuer.sendInfo(CoreMessageKeys.CMD_BAN_LOWER_RANK);
                        return;
                    }
                    issuer.sendInfo(CoreMessageKeys.CMD_TEMPBAN_BANNED, "{0}", iCloudOfflinePlayers.get(0).getName(),
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
