package de.cloud.core.bungee.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.bungee.contexts.OnlinePlayer;
import de.cloud.core.api.CoreAPI;
import de.cloud.core.bungee.MainBungee;
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


}
