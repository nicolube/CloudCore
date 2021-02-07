package de.cloud.core.ts3bot.models

import de.cloud.core.api.config.DatabaseConfig
import de.cloud.core.com.client.Config

data class ConfigModel(
    val host: String,
    val port: Int,
    val selectPort: Int,
    val nickName: String,
    val username: String,
    val password: String,
    val comConfig: Config,
    val database: DatabaseConfig,
)