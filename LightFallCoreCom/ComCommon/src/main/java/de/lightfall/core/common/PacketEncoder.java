package de.lightfall.core.common;

import de.lightfall.core.common.packet.Packet;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class PacketEncoder extends MessageToByteEncoder<Packet> {
    
    @Override
    protected void encode(ChannelHandlerContext ctx, Packet packet, ByteBuf out) throws Exception {
        System.out.println("Encode "+packet.getClass().getSimpleName());
        int id = PacketRegistry.getPacketID(packet.getClass());
        if (id < 0) throw new IllegalArgumentException("The Packet no valid packet ID.");
        out.writeInt(id);
        packet.write(out);
    }
    
}
