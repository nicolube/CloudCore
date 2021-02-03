/*
 * Copyright (C) 2020 nicolube
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.cloud.core.com.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.j256.ormlite.dao.Dao;
import de.cloud.core.com.server.handlers.NetworkHandler;
import de.cloud.core.common.PacketDecoder;
import de.cloud.core.common.PacketEncoder;
import de.cloud.core.common.models.InterComTokenModel;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import lombok.Getter;
import lombok.Setter;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemWriter;
import sun.security.tools.keytool.CertAndKeyGen;
import sun.security.x509.X500Name;

import javax.net.ssl.SSLException;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.security.*;

import java.security.cert.CertificateException;
import java.util.logging.Logger;

public class Server {

    public static boolean EPPLL = Epoll.isAvailable();
    @Setter @Getter
    private static Logger LOGGER = Logger.getLogger(Server.class.getName());

    private final Config config;
    private final InputStream certInputStream;
    private final InputStream privkeyInputStream;
    @Getter
    private final Dao<InterComTokenModel,Long> dao;

    public Server(File configDir, Dao<InterComTokenModel,Long> dao) throws IOException, NoSuchProviderException {
        this.dao = dao;
        if (!configDir.exists()) {
            configDir.mkdirs();
        }
        File configFile = new File(configDir, "config.json");
        if (!configFile.exists()) {
            LOGGER.info("Copy default config out of jar");
            Files.copy(getClass().getResourceAsStream("/server/config.json"), configFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }
        File csrCertFile = new File(configDir, "csr.pem");
        File privkeyCertFile = new File(configDir, "privkey.pem");
        if (!csrCertFile.exists() || !privkeyCertFile.exists()) {
            try {
                LOGGER.info("Generate X509 certificate pair....");
                CertAndKeyGen keypair = new CertAndKeyGen("RSA", "SHA1WithRSA", null);
                X500Name x500Name = new X500Name("ComServer", "None", "None", "None", "None", "None");
                keypair.generate(4096);
                FileOutputStream fileOutputStream = new FileOutputStream(privkeyCertFile);
                saveCert(privkeyCertFile, "PRIVATE KEY", keypair.getPrivateKey().getEncoded());
                saveCert(csrCertFile, "CERTIFICATE", keypair.getSelfCertificate(x500Name, 946944000).getEncoded());
                LOGGER.info("Certificate generated! Pleas copy the csr.pem to all clients.");
            } catch (InvalidKeyException | NoSuchAlgorithmException | CertificateException | SignatureException e) {
                e.printStackTrace();
            }
        }
        this.certInputStream = new FileInputStream(csrCertFile);
        this.privkeyInputStream = new FileInputStream(privkeyCertFile);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        this.config = gson.fromJson(new FileReader(configFile), Config.class);
    }

    public void startServer() {
        LOGGER.info("Starting server...");
        EventLoopGroup eventLoopGroup = EPPLL ? new EpollEventLoopGroup() : new NioEventLoopGroup();
        try {
            SslContext sslContext = SslContextBuilder.forServer(this.certInputStream, this.privkeyInputStream).build();
            ChannelFuture channelFuture = new ServerBootstrap()
                    .group(eventLoopGroup)
                    .channel(EPPLL ? EpollServerSocketChannel.class : NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<Channel>() {
                        @Override
                        protected void initChannel(Channel ch) {
                            ch.pipeline()
                                    .addLast("ssl", sslContext.newHandler(ch.alloc()))
                                    .addLast(new PacketEncoder())
                                    .addLast(new PacketDecoder())
                                    .addLast(new NetworkHandler(Server.this));
                        }
                    })
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .option(ChannelOption.SO_BACKLOG, 50)
                    .bind(config.getHost(), config.getPort());
            LOGGER.info("Server started!");
            channelFuture.sync().channel().closeFuture().syncUninterruptibly();
        } catch (InterruptedException | SSLException e) {
            e.printStackTrace();
        } finally {
            eventLoopGroup.shutdownGracefully();
        }
    }

    public void disconnect(Channel channel) {

    }

    private void saveCert(File file, String comment, byte[] encoded) throws IOException {
        PemObject pemObject = new PemObject(comment, encoded);
        PemWriter pemWriter = new PemWriter(new FileWriter(file));
        pemWriter.writeObject(pemObject);
        pemWriter.flush();
        pemWriter.close();
    }

}
