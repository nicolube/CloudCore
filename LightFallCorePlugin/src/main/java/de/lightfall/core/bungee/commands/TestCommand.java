package de.lightfall.core.bungee.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.bungee.contexts.OnlinePlayer;
import de.lightfall.core.api.CoreMessageKeys;
import de.lightfall.core.api.CoreAPI;
import de.lightfall.core.bungee.MainBungee;
import net.md_5.bungee.api.connection.ProxiedPlayer;

@CommandAlias("test")
@CommandPermission("core.test")
public class TestCommand extends BaseCommand {
    private final MainBungee plugin;

    public TestCommand(MainBungee plugin) {
        this.plugin = plugin;
    }

    @Subcommand("teleport")
    @CommandCompletion("@players")
    public void onTeleport(ProxiedPlayer player, OnlinePlayer target) {
        CoreAPI.getInstance().getUserManager().getUser(player.getUniqueId()).moveToPlayerTeleport(target.getPlayer().getUniqueId());
    }

    @Subcommand("messages")
    public void onMessages() {
        getCurrentCommandIssuer().sendInfo(CoreMessageKeys.CMD_KILL_TASK_NO_GROUP);
        getCurrentCommandIssuer().sendInfo(CoreMessageKeys.CMD_KILL_TASK_STOPPED);
    }
}
