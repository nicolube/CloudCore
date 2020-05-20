package de.lightfall.core.bungee.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Description;
import de.lightfall.core.api.message.CoreMessageKeys;
import net.md_5.bungee.api.connection.ProxiedPlayer;

@CommandAlias("ping")
public class PingCommand extends BaseCommand {

    @Default
    @Description("{@@core.cmd_ping_description}")
    public void onPing(ProxiedPlayer sender) {
        getCurrentCommandIssuer().sendInfo(CoreMessageKeys.CMD_PING_RESPONSE,"{0}",String.valueOf(sender.getPing()));
    }

}
