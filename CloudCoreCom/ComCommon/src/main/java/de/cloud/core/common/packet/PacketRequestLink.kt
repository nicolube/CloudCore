package de.cloud.core.common.packet

import de.cloud.core.api.ClientType
import de.cloud.core.common.LinkStatus
import io.netty.buffer.ByteBuf
import lombok.NoArgsConstructor

data class PacketOutRequestLink(
    val ip: String,
    val dbId: Int,
    val username: String,
    val type: ClientType,
    ) : Packet() {

    override fun read(byteBuf: ByteBuf?) {
        return autoRead(this, byteBuf)
    }

    override fun write(byteBuf: ByteBuf?) {
        return autoWrite(this, byteBuf)
    }

}

@NoArgsConstructor
data class PacketInRequestLink(
    val dbId: Int,
    val modelId: Long,
    val type: ClientType,
    val stats: LinkStatus,
) : Packet() {


    override fun read(byteBuf: ByteBuf?) {
        return autoRead(this, byteBuf)
    }

    override fun write(byteBuf: ByteBuf?) {
        return autoWrite(this, byteBuf)
    }
}