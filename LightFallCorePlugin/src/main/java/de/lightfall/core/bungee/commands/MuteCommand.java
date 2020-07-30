package de.lightfall.core.bungee.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.MessageKeys;
import co.aikar.commands.annotation.*;
import de.dytanic.cloudnet.CloudNet;
import de.dytanic.cloudnet.ext.bridge.player.IPlayerManager;
import de.lightfall.core.api.message.CoreMessageKeys;
import de.lightfall.core.bungee.MainBungee;
import de.lightfall.core.bungee.usermanager.BungeeCloudUser;

import java.util.UUID;
import java.util.concurrent.ExecutionException;

@CommandPermission("core.punish.mute")
@CommandAlias("mute")
public class MuteCommand extends BaseCommand {

    private final MainBungee plugin;

    public MuteCommand(MainBungee plugin) {
        this.plugin = plugin;
    }

    @Default
    @Description("{@@core.cmd_mute_description}")
    @Syntax("{@@core.cmd_mute_syntax}")
    @CommandCompletion("@cloudPlayers @nothing")
    public void onMute(BungeeCloudUser sender, @Single String offlinePlayerName, @Optional String reason) {
        CommandIssuer issuer = getCurrentCommandIssuer();
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
                offlineCloudUser.mute(sender, null, lReason);
                issuer.sendInfo(CoreMessageKeys.CMD_MUTE_MUTED, "{0}", iCloudOfflinePlayers.get(0).getName(), "{1}", lReason);
                } catch (ExecutionException | InterruptedException ex) {
                    ex.printStackTrace();
                }
            });
        });
    }

}
