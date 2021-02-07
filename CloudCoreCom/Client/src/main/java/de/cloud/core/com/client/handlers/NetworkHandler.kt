package de.cloud.core.com.client.handlers

import de.cloud.core.com.client.BotListener
import de.cloud.core.com.client.Client
import de.cloud.core.com.client.MinecraftListener
import de.cloud.core.common.packet.Packet
import de.cloud.core.common.packet.PacketOutAuthentication
import de.cloud.core.common.packet.PacketOutLink
import de.cloud.core.common.packet.PacketOutRequestLink
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
import java.util.function.Consumer

class NetworkHandler(val client: Client) : SimpleChannelInboundHandler<Packet>() {

    override fun channelRead0(ctx: ChannelHandlerContext, msg: Packet) {
        println(msg.javaClass)
        when (msg) {
            is PacketOutAuthentication -> client.connect(msg)
            is PacketOutLink -> client.listeners.forEach(Consumer { l -> if (l is BotListener) l.onLink(msg) })
            is PacketOutRequestLink -> client.listeners.forEach(Consumer { l ->
                if (l is MinecraftListener) l.onLinkRequest(msg)
            })
        }
    }

    override fun channelUnregistered(ctx: ChannelHandlerContext?) {
        super.channelUnregistered(ctx)
        println("Disconnected")
    }
}