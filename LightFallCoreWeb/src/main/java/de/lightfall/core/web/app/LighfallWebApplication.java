package de.lightfall.core.web.app;

import de.lightfall.core.web.rest.LighfallRestService;
import java.util.HashSet;
import java.util.Set;
import javax.ws.rs.core.Application;

public class LighfallWebApplication extends Application {
    private Set<Object> singletons = new HashSet();
    Set<Class<?>> resources = new HashSet();

    public LighfallWebApplication() {
        this.resources.add(AuthenticationFilter.class);
        this.singletons.add(new LighfallRestService("kakhaufen"));
    }

    public Set<Object> getSingletons() {
        return this.singletons;
    }

    public Set<Class<?>> getClasses() {
        return this.resources;
    }
}