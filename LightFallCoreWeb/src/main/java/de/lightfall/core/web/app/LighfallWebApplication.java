package de.lightfall.core.web.app;

import de.lightfall.core.common.DatabaseProvider;
import de.lightfall.core.web.app.config.Config;
import de.lightfall.core.web.rest.LighfallRestService;
import de.lightfall.core.web.rest.TeamService;
import de.lightfall.core.web.rest.UserService;
import lombok.Getter;
import lombok.SneakyThrows;
import me.lucko.luckperms.external.LPExternalBootstrap;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;

import javax.ws.rs.core.Application;
import java.io.File;
import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.HashSet;
import java.util.Set;

public class LighfallWebApplication extends Application {

    private final LuckPerms luckPerms;
    private final Config config;
    private final DatabaseProvider databaseProvider;
    @Getter
    private final Set<Object> singletons = new HashSet<>();
    @Getter
    private final Set<Class<?>> classes = new HashSet<>();

    @SneakyThrows
    public LighfallWebApplication() {
        File configDir = new File("LightFallCoreWeb");
        configDir.mkdir();
        File configFile = new File(configDir, "config.json");
        System.out.println(configFile.getAbsolutePath());
        if (!configFile.exists())
            Files.copy(getClass().getResourceAsStream("/webConfig.json"), configFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        this.config = Util.getGSON().fromJson(new FileReader(configFile), Config.class);
        this.databaseProvider = new DatabaseProvider(this.config.getDatabase());
        this.databaseProvider.setupWebApi();
        new LPExternalBootstrap(new File(configDir, "LuckPerms"));
        this.luckPerms = LuckPermsProvider.get();
        this.registerClasses();
        this.registerSingletons();
    }

    private void registerSingletons() {
        this.singletons.add(new TeamService(this.luckPerms, this.databaseProvider));
        this.singletons.add(new UserService(this.luckPerms, this.databaseProvider));
        this.singletons.add(new AuthenticationFilter(this.databaseProvider));
    }

    private void registerClasses() {
;        this.classes.add(LighfallRestService.class);
    }

}