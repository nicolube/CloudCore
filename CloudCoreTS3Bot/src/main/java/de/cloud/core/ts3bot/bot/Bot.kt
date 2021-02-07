package de.cloud.core.ts3bot.bot

import com.github.theholywaffle.teamspeak3.TS3Api
import com.github.theholywaffle.teamspeak3.TS3ApiAsync
import com.github.theholywaffle.teamspeak3.TS3Config
import com.github.theholywaffle.teamspeak3.TS3Query
import com.github.theholywaffle.teamspeak3.api.event.TS3EventType
import de.cloud.core.com.client.Client
import de.cloud.core.common.DatabaseProvider
import de.cloud.core.ts3bot.LOGGER
import de.cloud.core.ts3bot.models.ConfigModel
import java.util.*

class Bot(val config: ConfigModel, val messages: Map<Locale, ResourceBundle>, val client: Client, val databaseProvider: DatabaseProvider) {

    val api: TS3Api
    val asyncApi: TS3ApiAsync
    val commandManager: CommandManager
    val userManager: UserManager

    init {
        LOGGER.info("Init Bot...")
        val botListener = BotListener(this)
        val ts3conf = TS3Config()
        ts3conf.setHost(config.host)
        ts3conf.setQueryPort(config.port)
        ts3conf.setFloodRate(TS3Query.FloodRate.DEFAULT)
        LOGGER.info("Connecting Bot...")
        val query = TS3Query(ts3conf)
        query.connect()
        api = query.api
        asyncApi = query.asyncApi
        api.login(config.username, config.password)
        api.selectVirtualServerByPort(config.selectPort, config.nickName)
        client.addListener(botListener)
        LOGGER.info("Starting user manager...")
        userManager = UserManager(this)
        LOGGER.info("Starting command manager...")
        commandManager = CommandManager(this)
        api.addTS3Listeners(botListener)
        api.registerEvent(TS3EventType.CHANNEL)
        api.registerEvent(TS3EventType.TEXT_PRIVATE)

        Runtime.getRuntime().addShutdownHook(Thread {
            query.exit()
            println("Exit bot")
        })

    }
}