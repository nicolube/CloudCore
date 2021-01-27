package de.cloud.core.com.client;

import de.cloud.core.com.client.handlers.NetworkHandler;
import de.cloud.core.common.PacketDecoder;
import de.cloud.core.common.PacketEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.MultithreadEventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import lombok.Getter;
import lombok.SneakyThrows;

import java.util.concurrent.CompletableFuture;

public abstract class Client {

    public static boolean EPPLL = Epoll.isAvailable();

    private final Config config;
    @Getter
    private MultithreadEventLoopGroup eventLoopGroup;
    private Channel channel;

    public Client() {
        this.config = new Config();
        Runtime.getRuntime().addShutdownHook(new Thread(this::logout));

    }

    @SneakyThrows
    public void initChannel() {
        this.eventLoopGroup = EPPLL ? new EpollEventLoopGroup() : new NioEventLoopGroup();
        CompletableFuture.runAsync(() -> {
            try {
                SslContext sslContext = SslContextBuilder.forClient().trustManager(getClass().getResourceAsStream("/csr.pem")).build();
                this.channel = new Bootstrap()
                        .group(eventLoopGroup)
                        .channel(EPPLL ? EpollSocketChannel.class : NioSocketChannel.class)
                        .option(ChannelOption.SO_KEEPALIVE, true)
                        .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 3000)
                        .handler(new ChannelInitializer<Channel>() {
                            @Override
                            protected void initChannel(Channel ch) throws Exception {
                                ch.pipeline()
                                        .addLast("ssl", sslContext.newHandler(ch.alloc()))
                                        .addLast(new PacketDecoder())
                                        .addLast(new PacketEncoder())
                                        .addLast(new NetworkHandler(Client.this));
                            }
                        })
                        .connect(config.getHost(), config.getPort()).sync().channel();
                this.channel.closeFuture().syncUninterruptibly();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                eventLoopGroup.shutdownGracefully();
            }
        });
    }


    public void logout() {
        if (this.channel.isOpen()) {
            this.channel.disconnect();
            this.channel.close();
            this.eventLoopGroup.shutdownGracefully();
        }
    }
}
