package de.lightfall.core.com.client.handlers;

import de.lightfall.core.com.client.Client;
import de.lightfall.core.common.packet.Packet;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class NetworkHandler extends SimpleChannelInboundHandler<Packet> {

    private final Client client;

    public NetworkHandler(Client client) {
        this.client = client;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Packet packet) throws Exception {

    }
}
