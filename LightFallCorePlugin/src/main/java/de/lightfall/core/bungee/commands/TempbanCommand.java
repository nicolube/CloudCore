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

@CommandAlias("tempban")
@CommandPermission("system.punishments.command.tempban")
public class TempbanCommand extends BaseCommand {

    private final MainBungee plugin;

    public TempbanCommand(MainBungee plugin) {
        this.plugin = plugin;
    }

    @Default
    @CommandCompletion("@cloudPlayers")
    @Description("{@@core.cmd_tempban_description}")
    @Syntax("{@@core.cmd_tempban_syntax}")
    @CommandPermission("system.punishments.noreason")
    public void onTempBan(BungeeCloudUser sender, OnlinePlayer onlinePlayer, String time) {
        CommandIssuer issuer = getCurrentCommandIssuer();
        String reason = "Kein Grund angegeben / No reason given";
        Long timeInSeconds = parseString(time);

        if (timeInSeconds == null) {
            issuer.sendError(CoreMessageKeys.TIMEFORMAT_FAIL);
            return;
        }

        issuer.sendInfo(CoreMessageKeys.BANNED_PLAYER, "{0}", onlinePlayer.getPlayer().getName(),
                "{1}", Util.formatDate(new Date(TimeUnit.SECONDS.toMillis(timeInSeconds) + System.currentTimeMillis()), sender.getLocale()),
                "{2}", reason);
        CoreAPI.getInstance().getUserManager().getUser(onlinePlayer.getPlayer().getUniqueId()).tempMute(
                sender, null, timeInSeconds, reason
        );

    }

    @Default
    @CommandCompletion("@cloudPlayers")
    @Description("{@@core.cmd_tempban_description}")
    @Syntax("{@@core.cmd_tempban_syntax}")
    public void onTempBan(BungeeCloudUser sender, OnlinePlayer onlinePlayer, String time, String reason) {
        CommandIssuer issuer = getCurrentCommandIssuer();
        Long timeInSeconds = parseString(time);

        if (timeInSeconds == null) {
            issuer.sendError(CoreMessageKeys.TIMEFORMAT_FAIL);
            return;
        }

        issuer.sendInfo(CoreMessageKeys.BANNED_PLAYER, "{0}", onlinePlayer.getPlayer().getName(),
                "{1}", Util.formatDate(new Date(TimeUnit.SECONDS.toMillis(timeInSeconds) + System.currentTimeMillis()), sender.getLocale()),
                "{2}", reason);
        CoreAPI.getInstance().getUserManager().getUser(onlinePlayer.getPlayer().getUniqueId()).tempMute(
                sender, null, timeInSeconds, reason
        );

    }

    @Default
    @CommandCompletion("@cloudPlayers")
    @Description("{@@core.cmd_tempban_description}")
    @Syntax("{@@core.cmd_tempban_syntax}")
    @CommandPermission("system.punishments.noreason")
    public void onBan(BungeeCloudUser sender, String player, String time) {
        CommandIssuer issuer = getCurrentCommandIssuer();
        String reason = "Kein Grund angegeben / No reason given";
        Long timeInSeconds = parseString(time);

        if (timeInSeconds == null) {
            issuer.sendError(CoreMessageKeys.TIMEFORMAT_FAIL);
            return;
        }

        BridgePlayerManager.getInstance().getOfflinePlayerAsync(player).onComplete((listITask, iCloudOfflinePlayers) -> {
            if (iCloudOfflinePlayers.isEmpty()) {
                issuer.sendError(MessageKeys.COULD_NOT_FIND_PLAYER);
                return;
            }
            final UUID uniqueId = iCloudOfflinePlayers.get(0).getUniqueId();
            this.plugin.getUserManager().loadUser(uniqueId).thenAccept(offlineCloudUser -> {
                issuer.sendInfo(CoreMessageKeys.BANNED_PLAYER, "{0}", iCloudOfflinePlayers.get(0).getName(),
                        "{1}", Util.formatDate(new Date(TimeUnit.SECONDS.toMillis(timeInSeconds) + System.currentTimeMillis()), sender.getLocale()),
                        "{2}", reason);
                offlineCloudUser.tempMute(
                        sender, null, timeInSeconds, reason
                );
            });
        });
    }

    @Default
    @CommandCompletion("@cloudPlayers")
    @Description("{@@core.cmd_tempban_description}")
    @Syntax("{@@core.cmd_tempban_syntax}")
    public void onBan(BungeeCloudUser sender, String player, String time, String reason) {
        CommandIssuer issuer = getCurrentCommandIssuer();
        Long timeInSeconds = parseString(time);

        if (timeInSeconds == null) {
            issuer.sendError(CoreMessageKeys.TIMEFORMAT_FAIL);
            return;
        }

        BridgePlayerManager.getInstance().getOfflinePlayerAsync(player).onComplete((listITask, iCloudOfflinePlayers) -> {
            if (iCloudOfflinePlayers.isEmpty()) {
                issuer.sendError(MessageKeys.COULD_NOT_FIND_PLAYER);
                return;
            }
            final UUID uniqueId = iCloudOfflinePlayers.get(0).getUniqueId();
            this.plugin.getUserManager().loadUser(uniqueId).thenAccept(offlineCloudUser -> {
                issuer.sendInfo(CoreMessageKeys.BANNED_PLAYER, "{0}", iCloudOfflinePlayers.get(0).getName(),
                        "{1}", Util.formatDate(new Date(TimeUnit.SECONDS.toMillis(timeInSeconds) + System.currentTimeMillis()), sender.getLocale()),
                        "{2}", reason);
                offlineCloudUser.tempMute(
                        sender, null, timeInSeconds, reason
                );
            });
        });
    }


    private Long parseString(String string) {

        try {
            if (string.contains(",")) {
                long timeInSeconds = 0L;
                for (String part : string.split(",")) {
                    if (string.contains("s")) {
                        timeInSeconds += Long.parseLong(string.split("s")[0]);
                    }
                    if (string.contains("m")) {
                        timeInSeconds += TimeUnit.MINUTES.toSeconds(Long.parseLong(string.split("m")[0]));
                    }
                    if (string.contains("h")) {
                        timeInSeconds += TimeUnit.HOURS.toSeconds(Long.parseLong(string.split("h")[0]));
                    }
                    if (string.contains("d")) {
                        timeInSeconds += TimeUnit.DAYS.toSeconds(Long.parseLong(string.split("d")[0]));
                    }
                    if (string.contains("mo")) {
                        timeInSeconds += TimeUnit.DAYS.toSeconds(Long.parseLong(string.split("mo")[0]) * 30);
                    }
                }
                if (timeInSeconds == 0L || timeInSeconds < 0L) {
                    return null;
                }
                return timeInSeconds;
            } else {
                if (string.contains("s")) {
                    return Long.parseLong(string.split("s")[0]);
                }
                if (string.contains("m")) {
                    return TimeUnit.MINUTES.toSeconds(Long.parseLong(string.split("m")[0]));
                }
                if (string.contains("h")) {
                    return TimeUnit.HOURS.toSeconds(Long.parseLong(string.split("h")[0]));
                }
                if (string.contains("d")) {
                    return TimeUnit.DAYS.toSeconds(Long.parseLong(string.split("d")[0]));
                }
                if (string.contains("mo")) {
                    return TimeUnit.DAYS.toSeconds(Long.parseLong(string.split("mo")[0]) * 30);
                }
                return null;
            }
        } catch (Exception ignored) {
            return null;
        }
    }

}
