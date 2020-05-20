package de.lightfall.core.bungee.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.MessageKeys;
import co.aikar.commands.annotation.*;
import de.dytanic.cloudnet.ext.bridge.BridgePlayerManager;
import de.lightfall.core.api.message.CoreMessageKeys;
import de.lightfall.core.bungee.MainBungee;
import de.lightfall.core.bungee.usermanager.BungeeCloudUser;

import javax.swing.text.NumberFormatter;
import java.text.DecimalFormat;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

@CommandAlias("ban")
@CommandPermission("core.punish.ban")
public class BanCommand extends BaseCommand {

    private final MainBungee plugin;

    public BanCommand(MainBungee plugin) {
        this.plugin = plugin;
    }

    @Default
    @Description("{@@core.cmd_ban_description}")
    @Syntax("{@@core.cmd_ban_syntax}")
    @CommandCompletion("@cloudPlayers @nothing")
    public void onBan(BungeeCloudUser sender, @Single String offlinePlayerName, @Optional String reason) {
        CommandIssuer issuer = getCurrentCommandIssuer();
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
                    offlineCloudUser.ban(sender, null, lReason);
                    issuer.sendInfo(CoreMessageKeys.CMD_BAN_BANNED, "{0}", iCloudOfflinePlayers.get(0).getName(), "{1}", lReason);
                } catch (ExecutionException | InterruptedException ex) {
                    ex.printStackTrace();
                }
            });
        });
    }

}
