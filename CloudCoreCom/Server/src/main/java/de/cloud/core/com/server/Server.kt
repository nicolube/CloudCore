package de.cloud.core.com.server

import com.google.gson.GsonBuilder
import com.j256.ormlite.dao.Dao
import de.cloud.core.com.server.connections.ConnectionManager
import de.cloud.core.com.server.handlers.NetworkHandler
import de.cloud.core.common.PacketDecoder
import de.cloud.core.common.PacketEncoder
import de.cloud.core.common.models.InterComTokenModel
import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.Channel
import io.netty.channel.ChannelInitializer
import io.netty.channel.ChannelOption
import io.netty.channel.EventLoopGroup
import io.netty.channel.epoll.Epoll
import io.netty.channel.epoll.EpollEventLoopGroup
import io.netty.channel.epoll.EpollServerSocketChannel
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.nio.NioServerSocketChannel
import lombok.Getter
import lombok.Setter
import org.bouncycastle.util.io.pem.PemObject
import org.bouncycastle.util.io.pem.PemWriter
import sun.security.tools.keytool.CertAndKeyGen
import sun.security.x509.X500Name
import java.io.*
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import java.security.InvalidKeyException
import java.security.NoSuchAlgorithmException
import java.security.SignatureException
import java.security.cert.CertificateException
import java.util.logging.Logger


var EPPLL = Epoll.isAvailable()


class Server(configDir: File, val dao: Dao<InterComTokenModel, Long>) {
    private val config: Config
    private val certInputStream: InputStream
    private val privkeyInputStream: InputStream

    var connectionManager: ConnectionManager? = null

    companion object {
        @JvmStatic
        var LOGGER: Logger = Logger.getLogger(Server::class.java.name)
    }

    init {
        if (!configDir.exists()) {
            configDir.mkdirs()
        }
        val configFile = File(configDir, "config.json")
        if (!configFile.exists()) {
            LOGGER.info("Copy default config out of jar")
            Files.copy(
                javaClass.getResourceAsStream("/server/config.json"),
                configFile.toPath(),
                StandardCopyOption.REPLACE_EXISTING
            )
        }
        val csrCertFile = File(configDir, "csr.pem")
        val privkeyCertFile = File(configDir, "privkey.pem")
        if (!csrCertFile.exists() || !privkeyCertFile.exists()) {
            try {
                LOGGER.info("Generate X509 certificate pair....")
                val keypair = CertAndKeyGen("RSA", "SHA1WithRSA", null)
                val x500Name = X500Name("ComServer", "None", "None", "None", "None", "None")
                keypair.generate(4096)
                val fileOutputStream = FileOutputStream(privkeyCertFile)
                saveCert(privkeyCertFile, "PRIVATE KEY", keypair.privateKey.encoded)
                saveCert(csrCertFile, "CERTIFICATE", keypair.getSelfCertificate(x500Name, 946944000).encoded)
                LOGGER.info("Certificate generated! Pleas copy the csr.pem to all clients.")
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        certInputStream = FileInputStream(csrCertFile)
        privkeyInputStream = FileInputStream(privkeyCertFile)
        val gson = GsonBuilder().setPrettyPrinting().create()
        config = gson.fromJson(FileReader(configFile), Config::class.java)
    }

    fun startServer() {
        connectionManager = ConnectionManager(this)
        LOGGER.info("Starting server...")
        val eventLoopGroup: EventLoopGroup = if (EPPLL) EpollEventLoopGroup() else NioEventLoopGroup()
        try {
            //SslContext sslContext = SslContextBuilder.forServer(this.certInputStream, this.privkeyInputStream).build();
            val channelFuture = ServerBootstrap()
                .group(eventLoopGroup)
                .channel(if (EPPLL) EpollServerSocketChannel::class.java else NioServerSocketChannel::class.java)
                .childHandler(object : ChannelInitializer<Channel>() {
                    override fun initChannel(ch: Channel) {
                        ch.pipeline()
                            //.addLast("ssl", sslContext.newHandler(ch.alloc()))
                            .addLast(PacketEncoder())
                            .addLast(PacketDecoder())
                            .addLast(NetworkHandler(this@Server))
                        connectionManager!!.registerChannel(ch)
                    }
                })
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.SO_BACKLOG, 50)
                .bind(config.host, config.port)
            LOGGER.info("Server started!")
            channelFuture.sync().channel().closeFuture().syncUninterruptibly()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            eventLoopGroup.shutdownGracefully()
        }
    }

    fun disconnect(channel: Channel?) {
        connectionManager!!.disconnect(channel!!)
    }

    @Throws(IOException::class)
    private fun saveCert(file: File, comment: String, encoded: ByteArray) {
        val pemObject = PemObject(comment, encoded)
        val pemWriter = PemWriter(FileWriter(file))
        pemWriter.writeObject(pemObject)
        pemWriter.flush()
        pemWriter.close()
    }

}