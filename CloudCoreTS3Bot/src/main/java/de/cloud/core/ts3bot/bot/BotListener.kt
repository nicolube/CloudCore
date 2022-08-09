package de.cloud.core.ts3bot.bot

import com.github.theholywaffle.teamspeak3.api.event.*
import de.cloud.core.common.LinkStatus
import de.cloud.core.common.packet.PacketOutLink
import de.cloud.core.com.client.BotListener as ClientBotListener

class BotListener(private val bot: Bot) : TS3Listener, ClientBotListener {
    override fun onTextMessage(event: TextMessageEvent) {
        if (bot.api.whoAmI().id == event.invokerId) return
        bot.commandManager.chatEvent(event)
    }

    override fun onClientJoin(event: ClientJoinEvent) {
        if (event.clientType == 0)
            bot.userManager.onJoin(event)
    }

    override fun onClientLeave(event: ClientLeaveEvent) {
        bot.userManager.onLeave(event)
    }

    override fun onServerEdit(event: ServerEditedEvent) {

    }

    override fun onChannelEdit(event: ChannelEditedEvent) {

    }

    override fun onChannelDescriptionChanged(event: ChannelDescriptionEditedEvent) {

    }

    override fun onClientMoved(event: ClientMovedEvent) {

    }

    override fun onChannelCreate(event: ChannelCreateEvent) {

    }

    override fun onChannelDeleted(event: ChannelDeletedEvent) {

    }

    override fun onChannelMoved(event: ChannelMovedEvent) {

    }

    override fun onChannelPasswordChanged(event: ChannelPasswordChangedEvent) {

    }

    override fun onPrivilegeKeyUsed(event: PrivilegeKeyUsedEvent) {

    }

    override fun onLink(packet: PacketOutLink) {
        val user = bot.userManager.clients.values.find { it.dbId == packet.dbId } ?: return
        when (packet.status) {
            LinkStatus.SUCCESS -> {
                val userModels = bot.databaseProvider.userInfoDao.queryForEq("teamspeak_id", packet.dbId)
                if (userModels.isEmpty()) {
                    bot.userManager.sendMessage(user.id, "link.failed")
                    return
                }
                user.link(userModels[0], bot.asyncApi)
                bot.userManager.sendMessage(user.id, "link.success")
            }
            LinkStatus.NOT_ONLINE -> bot.userManager.sendMessage(user.id, "link.notOnline")
        }

    }
}