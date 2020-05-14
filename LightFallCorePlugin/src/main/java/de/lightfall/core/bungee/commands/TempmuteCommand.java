package de.lightfall.core.bungee.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.MessageKeys;
import co.aikar.commands.annotation.*;
import co.aikar.commands.bungee.contexts.OnlinePlayer;
import de.dytanic.cloudnet.ext.bridge.BridgePlayerManager;
import de.lightfall.core.api.CoreAPI;
import de.lightfall.core.api.Util;
import de.lightfall.core.api.message.CoreMessageKeys;
import de.lightfall.core.bungee.MainBungee;
import de.lightfall.core.bungee.usermanager.BungeeCloudUser;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;
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
    public void onMute(BungeeCloudUser sender, @Single String player, @Single String time, @Optional String reason) {
        CommandIssuer issuer = getCurrentCommandIssuer();
        Long timeInSeconds = Util.stringToMilesPhrase(time);

        if (timeInSeconds == null) {
            issuer.sendError(CoreMessageKeys.TIMEFORMAT_FAIL);
            return;
        }

        BridgePlayerManager.getInstance().getOfflinePlayerAsync(player).onComplete((listITask, iCloudOfflinePlayers) -> {
            String lReason;
            if (reason == null)
                lReason = "Kein Grund angegeben / No reason given";
            else
                lReason = reason;
            if (iCloudOfflinePlayers.isEmpty()) {
                issuer.sendError(MessageKeys.COULD_NOT_FIND_PLAYER);
                return;
            }
            final UUID uniqueId = iCloudOfflinePlayers.get(0).getUniqueId();
            this.plugin.getUserManager().loadUser(uniqueId).thenAccept(offlineCloudUser -> {
                issuer.sendInfo(CoreMessageKeys.MUTED_PLAYER, "{0}", iCloudOfflinePlayers.get(0).getName(),
                        "{1}", Util.formatDate(new Date(TimeUnit.SECONDS.toMillis(timeInSeconds) + System.currentTimeMillis()), sender.getLocale()),
                        "{2}", lReason);
                offlineCloudUser.tempMute(sender, null, timeInSeconds, lReason);
            });
        });
    }


}
