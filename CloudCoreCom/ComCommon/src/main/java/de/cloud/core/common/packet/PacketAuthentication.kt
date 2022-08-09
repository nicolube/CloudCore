package de.cloud.core.common.packet

import de.cloud.core.api.ClientType
import io.netty.buffer.ByteBuf
import lombok.NoArgsConstructor

@NoArgsConstructor
class PacketInAuthentication(
    var type: ClientType,
    var key: String,
    var comment: String
) : Packet() {
    override fun read(byteBuf: ByteBuf) {
        autoRead(this, byteBuf)
    }

    override fun write(byteBuf: ByteBuf) {
        autoWrite(this, byteBuf)
    }
}
