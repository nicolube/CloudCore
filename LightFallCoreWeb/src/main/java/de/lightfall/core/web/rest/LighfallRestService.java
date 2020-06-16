package de.lightfall.core.web.rest;

import de.lightfall.core.web.app.ResponseBuilder;
import de.lightfall.core.web.app.Secured;
import de.lightfall.core.web.entity.TestEntity;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/test/")
public class LighfallRestService {

    @GET
    @Path("default")
    @Produces({MediaType.APPLICATION_JSON})
    public Response getDefault() {
        return new ResponseBuilder().success("Hello world!").build();
    }

    @Secured
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    @Path("secured")
    public Response getSecured() {
        return new ResponseBuilder().success("'Hello world!' is now secured...").build();
    }
}
