package de.lightfall.core.web.app;

import com.sun.net.httpserver.HttpServer;
import de.lightfall.core.com.server.Server;
import de.lightfall.core.common.DatabaseProvider;
import de.lightfall.core.web.app.config.Config;
import de.lightfall.core.web.rest.LighfallRestService;
import de.lightfall.core.web.rest.TeamService;
import de.lightfall.core.web.rest.UserService;
import de.lightfall.core.web.rest.KeyService;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.java.Log;
import me.lucko.luckperms.external.LPExternalBootstrap;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import org.glassfish.jersey.jdkhttp.JdkHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

@Log
public class WebApplication {

    @Getter(value = AccessLevel.PROTECTED)
    private LPExternalBootstrap lpExternalBootstrap;
    private Server comServer;
    private LuckPerms luckPerms;
    private final Config config;
    @Getter
    private DatabaseProvider databaseProvider;
    @Getter
    private final Set<Object> singletons = new HashSet<>();
    @Getter
    private final Set<Class<?>> classes = new HashSet<>();
    CompletableFuture<Void> completableFuture;

    public static void main(String[] args) throws IOException {
        WebApplication webApplication = new WebApplication();
        ResourceConfig rc = new ResourceConfig();
        rc.packages("de.lightfall.core.web.provider", "de.lightfall.core.web.rest.resources");
        rc.packages("org.glassfish.jersey.jackson.internal.jackson.jaxrs.json");
        webApplication.getSingletons().forEach(s -> rc.registerInstances(s));
        HttpServer httpServer = JdkHttpServerFactory.createHttpServer(URI.create("http://localhost:8087"), rc);
        System.in.read();
        httpServer.stop(0);
    }

    @SneakyThrows
    public WebApplication() {
        File configDir = new File("LightFallCoreWeb");
        configDir.mkdir();
        File configFile = new File(configDir, "config.json");
        System.out.println(configFile.getAbsolutePath());
        if (!configFile.exists())
            Files.copy(getClass().getResourceAsStream("/webConfig.json"), configFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        this.config = Util.getGSON().fromJson(new FileReader(configFile), Config.class);

        log.info("Connect to Database...");
        this.databaseProvider = new DatabaseProvider(this.config.getDatabase());
        this.databaseProvider.setupWebApi();
        log.info("Setup luckperms external...");
        this.lpExternalBootstrap = new LPExternalBootstrap(new File(configDir, "LuckPerms"));;
        log.info("Get luckperms external...");
        this.luckPerms = LuckPermsProvider.get();
        log.info("Start comServer...");
        this.comServer = new Server(new File(configDir, "comServer"), databaseProvider.getInterComTokenDao());
        Server.setLOGGER(log);
        CompletableFuture.runAsync(() -> this.comServer.startServer());
        this.registerClasses();
        this.registerSingletons();
    }

    private void registerSingletons() {
        this.singletons.add(new TeamService(this.luckPerms, this.databaseProvider));
        this.singletons.add(new UserService(this.luckPerms, this.databaseProvider));
        this.singletons.add(new AuthenticationFilter(this.databaseProvider));
        this.singletons.add(new KeyService(this.databaseProvider));
        this.singletons.add(new LighfallRestService(this));
    }

    private void registerClasses() {
    }

    public boolean isReady() {
        return this.completableFuture.isDone();
    }




}
