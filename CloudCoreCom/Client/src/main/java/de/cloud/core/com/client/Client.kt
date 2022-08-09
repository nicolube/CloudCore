package de.cloud.core.com.client

import com.sun.istack.internal.logging.Logger
import de.cloud.core.api.ClientType
import de.cloud.core.com.client.handlers.NetworkHandler
import de.cloud.core.common.ConnectionStatus
import de.cloud.core.common.LinkStatus
import de.cloud.core.common.PacketDecoder
import de.cloud.core.common.PacketEncoder
import de.cloud.core.common.packet.*
import io.netty.bootstrap.Bootstrap
import io.netty.channel.Channel
import io.netty.channel.ChannelInitializer
import io.netty.channel.ChannelOption
import io.netty.channel.MultithreadEventLoopGroup
import io.netty.channel.epoll.Epoll
import io.netty.channel.epoll.EpollEventLoopGroup
import io.netty.channel.epoll.EpollSocketChannel
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.nio.NioSocketChannel
import java.util.*
import java.util.concurrent.CompletableFuture

val EPOLL = Epoll.isAvailable()

val LOGGER: Logger = Logger.getLogger(Client::class.java);

class Client(private val config: Config, val type: ClientType, private val comment: String) {

    private var eventLoopGroup: MultithreadEventLoopGroup? = null
    private var channel: Channel? = null
    private var running = false
    private var authenticated = false
    internal var listeners: MutableList<ClientListener> = mutableListOf()

    init {
        Runtime.getRuntime().addShutdownHook(Thread { this.logout() })
    }

    fun start() {
        CompletableFuture.runAsync {
            initChannel()
            while (authenticated) {
                Thread.sleep(5000)
                initChannel()
            }
        }
        Thread.sleep(1000)
    }

    private fun initChannel() {
        this.eventLoopGroup = if (EPOLL) EpollEventLoopGroup() else NioEventLoopGroup()
        //val sslContext: SslContext =
        //    SslContextBuilder.forClient().trustManager(javaClass.getResourceAsStream("/csr.pem")).build()
        LOGGER.warning("ComClient tries to connect to ${config.host}:${config.port}")
        try {
            this.channel = Bootstrap()
                .group(eventLoopGroup)
                .channel(if (EPOLL) EpollSocketChannel::class.java else NioSocketChannel::class.java)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 3000)
                .handler(object : ChannelInitializer<Channel>() {
                    override fun initChannel(ch: Channel) {
                        ch.pipeline()
                            //.addLast("ssl", sslContext.newHandler(ch.alloc()))
                            .addLast(PacketDecoder())
                            .addLast(PacketEncoder())
                            .addLast(NetworkHandler(this@Client))
                    }
                })
                .connect(config.host, config.port).sync().channel()
            val packetInAuthentication = PacketInAuthentication(type, config.key, comment)
            this.channel?.writeAndFlush(packetInAuthentication)
            this.running = true
            this.channel?.closeFuture()?.syncUninterruptibly()
        } catch (e: Exception) {
            LOGGER.warning("Failed to connect to Server!")
            e.printStackTrace()
        } finally {
            this.running = false
            this.eventLoopGroup?.shutdownGracefully()
        }
    }

    private fun logout() {
        if (channel != null && channel!!.isOpen) {
            channel!!.disconnect()
            channel!!.close()
            eventLoopGroup!!.shutdownGracefully()
        }
    }

    fun connect(msg: PacketOutAuthentication) {
        when (msg.status) {
            ConnectionStatus.CONNECTED -> {
                authenticated = true
                LOGGER.info("Authentication complete.")
            }
            ConnectionStatus.ACCESS_DENIED -> LOGGER.info("Authentication failed.")
        }
    }

    fun addListener(listener: ClientListener) {
        listeners.add(listener)
    }

    fun isReady() = running && authenticated && channel != null

    fun requestLink(ip: String, dbIp: Int, username: String, type: ClientType) {
        channel?.writeAndFlush(PacketInLink(ip, dbIp, username, type))
    }

    fun confirmLink(dbId: Int, modelId: Long, status: LinkStatus) {
        channel?.writeAndFlush(PacketInRequestLink(dbId, modelId, ClientType.MINECRAFT, status))
    }
}