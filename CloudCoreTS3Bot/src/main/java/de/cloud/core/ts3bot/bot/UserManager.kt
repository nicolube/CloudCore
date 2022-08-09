package de.cloud.core.ts3bot.bot

import com.github.theholywaffle.teamspeak3.TS3ApiAsync
import com.github.theholywaffle.teamspeak3.api.ClientProperty
import com.github.theholywaffle.teamspeak3.api.event.ClientJoinEvent
import com.github.theholywaffle.teamspeak3.api.event.ClientLeaveEvent
import de.cloud.core.common.models.UserInfoModel
import java.util.*
import java.util.function.Consumer

class User(
    val id: Int,
    val dbId: Int,
    userModel: UserInfoModel?,
    var locale: Locale
) {
    var uuid: UUID? = null
    var dbInfoId: Long? = null

    init {
        if (userModel != null) {
            uuid = userModel.uuid
            dbInfoId = userModel.id
        }
    }

    fun link(userModel: UserInfoModel, api: TS3ApiAsync) {
        uuid = userModel.uuid
        dbInfoId = userModel.id
        api.editClient(id, ClientProperty.CLIENT_DESCRIPTION, "${userModel.name} <$uuid>")
        // TODO: update groups
    }

    fun isLinked() = uuid != null

}

class UserManager(private val bot: Bot) {

    var clients: MutableMap<Int, User> = mutableMapOf()

    init {
        bot.api.clients.forEach(Consumer { loadUser(it.id) })
    }

    private fun loadUser(cId: Int) {
        bot.asyncApi.getClientInfo(cId).onSuccess { client ->
            val queryForEq = bot.databaseProvider.userInfoDao.queryForEq("teamspeak_id", client.databaseId)
            val locale = when (client.country) {
                "DE" -> Locale.GERMAN
                "AT" -> Locale.GERMAN
                "CH" -> Locale.GERMAN
                else -> Locale.ENGLISH
            }
            val user = if (queryForEq.isEmpty()) User(client.id, client.databaseId, null, locale)
            else {
                val data = queryForEq[0]
                User(cId, client.databaseId, data, Locale.forLanguageTag(data.locale))
            }
            clients[cId] = user
            if (!user.isLinked()) {
                sendMessage(cId, "notLinked")
            }
        }
    }

    fun sendMessage(id: Int, key: String, vararg replacers: String) {
        try {
            var msg = bot.messages[clients[id]?.locale]?.getString(key)
            if (replacers.isNotEmpty() && replacers.size % 2 == 0)
                for (i in replacers.indices step 2) msg = msg?.replace(replacers[i], replacers[i + 1])
            bot.asyncApi.sendPrivateMessage(id, msg)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun onJoin(event: ClientJoinEvent) {
        loadUser(event.clientId)
    }

    fun onLeave(event: ClientLeaveEvent) {
        clients.remove(event.clientId)
    }
}