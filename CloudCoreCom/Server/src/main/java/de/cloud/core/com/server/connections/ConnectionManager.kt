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
        Server.LOGGER.warning("${ch.remoteAddress()} tries to connect!")
        pendingConnections[ch] = ConnectionInstance(ch, ClientType.UNKNOWN)
    }

    fun acceptAuthPacket(ch: Channel, packet: PacketInAuthentication) {
        CompletableFuture.supplyAsync {
            server.dao.queryBuilder().where()
                .eq("token", packet.key).and().eq("type", packet.type).queryForFirst()
        }.thenAccept(Consumer { q ->
            if (q == null) {
                Server.LOGGER.warning("${ch.remoteAddress()} failed authentication!")
                ch.writeAndFlush(PacketOutAuthentication(ConnectionStatus.ACCESS_DENIED))
                ch.disconnect()
                ch.close()
                return@Consumer
            }
            pendingConnections.remove(ch).also {
                if (it == null) return@also
                it.connectionStats = ConnectionStatus.CONNECTED
                connections[q.type]!![ch] = it
                ch.writeAndFlush(PacketOutAuthentication(it.connectionStats))
                Server.LOGGER
                    .info("${ch.remoteAddress()} connected as ${q.type.name} with comment: ${packet.comment}")
            }
        })

    }

    fun disconnect(ch: Channel) {
        if (pendingConnections.remove(ch) == null)
            connections.values.forEach { if (it.remove(ch) != null) return@disconnect }
    }

    fun send(type: ClientType, packet: Packet): Int {
        var amont: Int = 0
        connections[type]?.forEach { (k, _) ->
            k.writeAndFlush(packet)
            amont++
        }
        return amont
    }
}

