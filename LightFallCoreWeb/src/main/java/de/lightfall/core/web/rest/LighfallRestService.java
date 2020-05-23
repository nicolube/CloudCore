package de.lightfall.core.web.rest;

import de.lightfall.core.web.app.ResponseBuilder;
import de.lightfall.core.web.app.Secured;
import de.lightfall.core.web.entity.TestEntity;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

@Path("/test/")
public class LighfallRestService {
    String test;

    public LighfallRestService(String test) {
        this.test = test;
    }

    @GET
    @Path("default")
    public Response hello() {
        return new ResponseBuilder().success("Hello world!").build();
    }

    @Secured
    @GET
    @Produces({"application/json"})
    @Path("secured")
    public Response test() {
        return new ResponseBuilder().success("'Hello world!' is now secured...").build();
    }
}
