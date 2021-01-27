package de.cloud.core.common;

import de.cloud.core.common.packet.Packet;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

public class PacketDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf byteBuf, List<Object> out) throws Exception {
        int id = byteBuf.readInt();
        Packet packet = PacketRegistry.values()[id].getPacketClass().newInstance();
        packet.read(byteBuf);
        out.add(packet);
    }
    
}
