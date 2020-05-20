package de.lightfall.core.bungee.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.MessageKeys;
import co.aikar.commands.annotation.*;
import de.dytanic.cloudnet.ext.bridge.BridgePlayerManager;
import de.lightfall.core.api.message.CoreMessageKeys;
import de.lightfall.core.bungee.MainBungee;
import de.lightfall.core.bungee.usermanager.BungeeCloudUser;

import java.util.UUID;
import java.util.concurrent.ExecutionException;


@CommandAlias("kick")
@CommandPermission("core.punish.kick")
public class KickCommand extends BaseCommand {

    private final MainBungee plugin;

    public KickCommand(MainBungee plugin) {
        this.plugin = plugin;
    }

    @Default
    @Description("{@@core.cmd_kick_description}")
    @Syntax("{@@core.cmd_kick_syntax}")
    @CommandCompletion("@cloudPlayers @nothing")
    public void onKick(BungeeCloudUser sender, @Single String offlinePlayerName, @Optional String reason) {
        CommandIssuer issuer = getCurrentCommandIssuer();
        final String reasonString = reason == null ? "Kein Grund angegeben / No reason given" : reason;

        BridgePlayerManager.getInstance().getOnlinePlayerAsync(offlinePlayerName).onComplete((listITask, iCloudOfflinePlayers) -> {
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
                    offlineCloudUser.kick(sender,null,reasonString);
                    issuer.sendInfo(CoreMessageKeys.CMD_KICK_KICKED, "{0}", iCloudOfflinePlayers.get(0).getName(), "{1}", reasonString);
                } catch (ExecutionException | InterruptedException ex) {
                    ex.printStackTrace();
                }
            });
        });
    }

}
