package de.cloud.core.ts3bot

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import de.cloud.core.api.ClientType
import de.cloud.core.com.client.Client
import de.cloud.core.common.DatabaseProvider
import de.cloud.core.ts3bot.bot.Bot
import de.cloud.core.ts3bot.models.ConfigModel
import de.nicolube.devutils.LoggerBuilder
import java.io.File
import java.io.FileOutputStream
import java.io.FileReader
import java.net.URLClassLoader
import java.util.*
import java.util.logging.FileHandler
import java.util.logging.Logger

val LOGGER: Logger = createLogger()
val GSON: Gson = GsonBuilder().setPrettyPrinting().create()

fun main() {
    val main = Main()
}

fun createLogger(): Logger {
    val logDir = File("logs")
    logDir.mkdirs()
    val logFile = File(logDir, "BotLog")
    val logger = LoggerBuilder(Any::class.java).setFile(logFile).setApplyFormatterToParent(true).build()
    val handler = logger.handlers.filter { (it is FileHandler) }[0]
    logger.removeHandler(handler)
    logger.parent.addHandler(handler)
    return logger
}

class Main {
    val config: ConfigModel
    val client: Client
    val databaseProvider: DatabaseProvider
    val bot: Bot
    val messages: MutableMap<Locale, ResourceBundle> = mutableMapOf()

    init {
        config = loadConfig()
        databaseProvider = DatabaseProvider(config.database)
        client = startComServer()

        bot = Bot(config, messages, client, databaseProvider);
    }

    private fun startComServer(): Client {
        val client = Client(config.comConfig, ClientType.MINECRAFT, "TS3Bot")
        client.start()
        return client
    }

    private fun loadConfig(): ConfigModel {
        LOGGER.info("Setup config...")
        val configDir = File("config")
        configDir.mkdirs()
        val configFile = File(configDir, "config.json")
        if (!configFile.exists()) {
            LOGGER.info("Copy default config out of jar")
            copyFile(configFile.name, configDir)
        }
        copyFile("teamspeak_en.properties", configDir)
        copyFile("teamspeak_de.properties", configDir)
        messages[Locale.ENGLISH] =
            ResourceBundle.getBundle(
                "teamspeak", Locale.ENGLISH,
                URLClassLoader(arrayOf(configDir.toURI().toURL()))
            )
        messages[Locale.GERMAN] =
            ResourceBundle.getBundle(
                "teamspeak", Locale.GERMAN,
                URLClassLoader(arrayOf(configDir.toURI().toURL()))
            )
        return GSON.fromJson(FileReader(configFile), ConfigModel::class.java)
    }

    private fun copyFile(name: String, dir: File) {
        val file = File(dir, name)
        if (!file.exists())
            Main::class.java.getResourceAsStream("/$name").copyTo(FileOutputStream(file))
    }
}