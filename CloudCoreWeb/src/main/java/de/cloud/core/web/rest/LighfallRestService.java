package de.cloud.core.web.rest;

import de.cloud.core.web.app.WebApplication;
import de.cloud.core.web.app.ResponseBuilder;
import de.cloud.core.web.app.Secured;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("test/")
public class LighfallRestService {

    WebApplication app;

    public LighfallRestService(WebApplication app) {
        this.app = app;
    }

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

    @GET
    @Produces({MediaType.TEXT_PLAIN})
    @Path("ready")
    public Response isReady() {
        return Response.accepted().entity(app.isReady()).build();
    }
}
