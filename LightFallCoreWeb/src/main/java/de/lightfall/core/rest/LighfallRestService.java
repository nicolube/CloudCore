package de.lightfall.core.rest;

import de.lightfall.core.app.Secured;
import de.lightfall.core.app.TestEntity;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

@Path("/")
public class LighfallRestService {
    String test;

    public LighfallRestService(String test) {
        this.test = test;
    }

    @GET
    @Path("/hello")
    public Response hello() {
        return Response.status(200).entity("hello").build();
    }

    @Secured
    @GET
    @Produces({"application/json"})
    @Path("test/{id}")
    public Response test(@PathParam("id") Long id) {
        return Response.status(200).encoding("UTF-8").entity(new TestEntity(this.test)).build();
    }
}
