package de.cloud.core.com.server.handlers

import de.cloud.core.api.ClientType
import de.cloud.core.com.server.Server
import de.cloud.core.common.packet.*
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler

class NetworkHandler(val server: Server) : SimpleChannelInboundHandler<Packet>() {

    override fun channelRead0(ctx: ChannelHandlerContext, msg: Packet) {
        when (msg) {
            is PacketInAuthentication -> server.connectionManager.acceptAuthPacket(ctx.channel(), msg)
            is PacketInLink -> server.connectionManager.send(
                ClientType.MINECRAFT,
                PacketOutRequestLink(msg.ip, msg.dbId, msg.username, msg.type)
            )
            is PacketInRequestLink -> server.connectionManager.send(msg.type, PacketOutLink(msg.dbId, msg.stats))
        }
    }

    override fun channelUnregistered(ctx: ChannelHandlerContext) {
        super.channelUnregistered(ctx)
        server.disconnect(ctx.channel())
    }
}