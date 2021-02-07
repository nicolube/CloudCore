package de.cloud.core.com.client

import de.cloud.core.common.packet.*

interface MinecraftListener: ClientListener {
    fun onLinkRequest(packet: PacketOutRequestLink)
}

interface BotListener: ClientListener {
    fun onLink(packet: PacketOutLink)

}

interface ClientListener