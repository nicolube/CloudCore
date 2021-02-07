package de.cloud.core.ts3bot.bot

import com.github.theholywaffle.teamspeak3.api.TextMessageTargetMode
import com.github.theholywaffle.teamspeak3.api.event.TextMessageEvent
import com.github.theholywaffle.teamspeak3.api.wrapper.ClientInfo
import de.cloud.core.ts3bot.bot.commands.*

abstract class Command(protected var commandManager: CommandManager) {
    abstract val name: String
    abstract val aliases: List<String>

    abstract fun execute(client: ClientInfo, args: List<String>): Boolean

    abstract fun canUse(client: ClientInfo): Boolean
}

class CommandManager(val bot: Bot) {

    private val commands: MutableMap<String, Command> = mutableMapOf()
    private val commandAliases: MutableMap<String, Command> = mutableMapOf()

    init {
        registerCommands()
    }

    private fun registerCommands() {
        registerCommand(CommandChannelId(this))
        registerCommand(CommandLink(this))
    }

    fun registerCommand(cmd: Command) {
        commands[cmd.name.toLowerCase()] = cmd
        cmd.aliases.forEach { alias ->
            commandAliases[alias.toLowerCase()] = cmd
        }
    }

    fun chatEvent(event: TextMessageEvent) {
        if (event.targetMode != TextMessageTargetMode.CLIENT) return
        val message = event.message
        if (!message.startsWith('!')) return
        val args = message.substring(1).split(Regex("\\s+")).toMutableList()
        if (args.isEmpty()) return;
        val cmdName = args.removeAt(0).toLowerCase()
        var cmd = commands[cmdName]
        if (cmd == null) {
            cmd = commandAliases[cmdName]
        }
        if (cmd == null) {
            return
        }
        bot.asyncApi.getClientInfo(event.invokerId).onSuccess { client ->
            if (cmd.canUse(client)) cmd.execute(client, args)
        }

    }


}