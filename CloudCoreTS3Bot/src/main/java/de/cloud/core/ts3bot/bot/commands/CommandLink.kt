package de.cloud.core.ts3bot.bot.commands

import com.github.theholywaffle.teamspeak3.api.wrapper.ClientInfo
import de.cloud.core.api.ClientType
import de.cloud.core.ts3bot.bot.Command
import de.cloud.core.ts3bot.bot.CommandManager

class CommandLink(commandManager: CommandManager) : Command(commandManager) {
    override val name: String = "link"
    override val aliases: List<String> = emptyList()

    override fun execute(client: ClientInfo, args: List<String>): Boolean {
        if (args.isEmpty()) return false
        if (!commandManager.bot.client.isReady()) {
            commandManager.bot.userManager.sendMessage(client.id, "service.notAvailable")
        }
        commandManager.bot.client.requestLink(client.ip, client.databaseId, args[0], ClientType.TEAMSPEAK)
        return true
    }

    override fun canUse(client: ClientInfo): Boolean {
       return true
    }
}