package de.cloud.core.common

import de.cloud.core.common.PacketRegistry.Companion.getPacketID
import de.cloud.core.common.packet.Packet
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.ByteToMessageDecoder
import io.netty.handler.codec.MessageToByteEncoder

class PacketDecoder : ByteToMessageDecoder() {
    
    @Throws(Exception::class)
    override fun decode(ctx: ChannelHandlerContext, byteBuf: ByteBuf, out: MutableList<Any>) {
        val id = byteBuf.readInt()
        val packet = PacketRegistry.values()[id].packetClass.newInstance()
        println("Decode packet: " + packet.javaClass.simpleName)
        packet.read(byteBuf)
        out.add(packet)
    }
}


class PacketEncoder : MessageToByteEncoder<Packet?>() {

    @Throws(Exception::class)
    override fun encode(ctx: ChannelHandlerContext?, packet: Packet?, out: ByteBuf?) {
        println("Encode " + packet?.javaClass?.simpleName)
        val id = getPacketID(packet!!.javaClass)
        require(id >= 0) { "The Packet no valid packet ID." }
        out!!.writeInt(id)
        packet.write(out)
    }
}