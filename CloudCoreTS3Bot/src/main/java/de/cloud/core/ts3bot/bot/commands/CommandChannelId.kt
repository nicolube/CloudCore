package de.cloud.core.ts3bot.bot.commands

import com.github.theholywaffle.teamspeak3.api.wrapper.ClientInfo
import de.cloud.core.ts3bot.bot.Command
import de.cloud.core.ts3bot.bot.CommandManager

class CommandChannelId(commandManager: CommandManager) : Command(commandManager) {
    override val name: String = "channelId"
    override val aliases: List<String> = listOf("cid")

    override fun execute(client: ClientInfo, args: List<String>): Boolean {
        commandManager.bot.userManager.sendMessage(client.id, "cmd.channelId.execute", "{channelId}", client.channelId.toString())
        return true
    }

    override fun canUse(client: ClientInfo): Boolean {
        return true
    }
}