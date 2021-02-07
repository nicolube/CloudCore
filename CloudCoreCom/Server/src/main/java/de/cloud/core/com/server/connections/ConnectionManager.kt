package de.cloud.core.com.server.connections

import de.cloud.core.api.ClientType
import de.cloud.core.com.server.Server
import de.cloud.core.common.ConnectionStatus
import de.cloud.core.common.packet.Packet
import de.cloud.core.common.packet.PacketInAuthentication
import de.cloud.core.common.packet.PacketOutAuthentication
import io.netty.channel.Channel
import java.util.concurrent.CompletableFuture
import java.util.function.Consumer


class ConnectionManager(private val server: Server) {

    private val connections: MutableMap<ClientType, MutableMap<Channel, ConnectionInstance>> = mutableMapOf()
    private val pendingConnections: MutableMap<Channel, ConnectionInstance> = mutableMapOf()

    init {
        ClientType.values().forEach { this.connections[it] = mutableMapOf() }
    }

    fun registerChannel(ch: Channel) {
        Server.getLOGGER().warning("${ch.remoteAddress()} tries to connect!")
        pendingConnections[ch] = ConnectionInstance(ch, ClientType.UNKNOWN)
    }

    fun acceptAuthPacket(ch: Channel, packet: PacketInAuthentication) {
        CompletableFuture.supplyAsync {
            server.dao.queryBuilder().where()
                .eq("token", packet.key).and().eq("type", packet.type).queryForFirst()
        }.thenAccept(Consumer { q ->
            val outPacket = PacketOutAuthentication()
            if (q == null) {
                Server.getLOGGER().warning("${ch.remoteAddress()} failed authentication!")
                outPacket.status = ConnectionStatus.ACCESS_DENIED
                ch.writeAndFlush(outPacket)
                ch.disconnect()
                ch.close()
                return@Consumer
            }
            pendingConnections.remove(ch).also {
                if (it == null) return@also
                it.connectionStats = ConnectionStatus.CONNECTED
                connections[q.type]!![ch] = it
                outPacket.status = it.connectionStats
                ch.writeAndFlush(outPacket)
                Server.getLOGGER()
                    .info("${ch.remoteAddress()} connected as ${q.type.name} with comment: ${packet.comment}")
            }
        })

    }

    fun disconnect(ch: Channel) {
        if (pendingConnections.remove(ch) == null)
            connections.values.forEach { if (it.remove(ch) != null) return@disconnect }
    }

    fun send(type:ClientType, packet: Packet) {
        connections[type]?.forEach { (k, v) ->  k.writeAndFlush(packet) }
    }
}

