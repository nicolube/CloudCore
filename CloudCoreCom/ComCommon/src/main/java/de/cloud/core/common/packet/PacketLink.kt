package de.cloud.core.common.packet

import de.cloud.core.api.ClientType
import de.cloud.core.common.LinkStatus
import io.netty.buffer.ByteBuf
import lombok.NoArgsConstructor

@NoArgsConstructor
data class PacketInLink(
    val ip: String,
    val dbId: Int,
    val username: String,
    val type: ClientType
): Packet() {


    override fun read(byteBuf: ByteBuf?) {
        return autoRead(this, byteBuf)
    }

    override fun write(byteBuf: ByteBuf?) {
        return autoWrite(this, byteBuf)
    }
}

@NoArgsConstructor
data class PacketOutLink(
    val dbId: Int,
    val stauts: LinkStatus,
): Packet() {


    override fun read(byteBuf: ByteBuf?) {
        return autoRead(this, byteBuf)
    }

    override fun write(byteBuf: ByteBuf?) {
        return autoWrite(this, byteBuf)
    }
}