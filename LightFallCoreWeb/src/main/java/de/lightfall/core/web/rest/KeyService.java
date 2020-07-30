package de.lightfall.core.web.rest;


import de.lightfall.core.api.ClientType;
import de.lightfall.core.common.DatabaseProvider;
import de.lightfall.core.common.models.InterComTokenModel;
import de.lightfall.core.common.models.WebApiTokenModel;
import de.lightfall.core.web.app.ResponseBuilder;
import de.lightfall.core.web.app.Secured;
import lombok.SneakyThrows;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/keys/")
public class KeyService {
    private final DatabaseProvider databaseProvider;

    public KeyService(DatabaseProvider databaseProvider) {
        this.databaseProvider = databaseProvider;
    }

    @SneakyThrows
    @GET
    @Secured
    @Produces({"application/json"})
    @Path("web")
    public Response getWebAPIKey() {
        List<WebApiTokenModel> webApiTokenModels = this.databaseProvider.getWebApiTokenDao().queryForAll();
        return new ResponseBuilder().success(webApiTokenModels).build();
    }


    @SneakyThrows
    @PUT
    @Secured
    @Produces({"application/json"})
    @Consumes({"application/json"})
    @Path("web")
    public Response putWebAPIKey(WebApiTokenModel webApiTokenModel) {
        WebApiTokenModel ifNotExists = this.databaseProvider.getWebApiTokenDao().createIfNotExists(webApiTokenModel);
        return new ResponseBuilder().success(ifNotExists).build();
    }

    @SneakyThrows
    @DELETE
    @Secured
    @Produces({"application/json"})
    @Path("web")
    public Response deleteWebAPIKey(@QueryParam("id") Long id) {
        int i = this.databaseProvider.getWebApiTokenDao().deleteById(id);
        return new ResponseBuilder().success(i).build();
    }

    @SneakyThrows
    @GET
    @Secured
    @Produces({"application/json"})
    @Path("inter_types")
    public Response getInterKeyTypes() {
        ClientType[] values = ClientType.values();
        String[] r = new String[values.length];
        for (int i = 0; i < values.length; i++) {
            r[i] = values[i].name();
        }
        return new ResponseBuilder().success(values).build();
    }


    @SneakyThrows
    @GET
    @Secured
    @Produces({"application/json"})
    @Path("inter")
    public Response getInterKey() {
        List<InterComTokenModel> interComTokenModels = this.databaseProvider.getInterComTokenDao().queryForAll();
        return new ResponseBuilder().success(interComTokenModels).build();
    }


    @SneakyThrows
    @PUT
    @Secured
    @Produces({"application/json"})
    @Consumes({"application/json"})
    @Path("inter")
    public Response putInterKey(InterComTokenModel interComTokenModel) {
        InterComTokenModel ifNotExists = this.databaseProvider.getInterComTokenDao().createIfNotExists(interComTokenModel);
        return new ResponseBuilder().success(ifNotExists).build();
    }

    @SneakyThrows
    @DELETE
    @Secured
    @Produces({"application/json"})
    @Path("inter")
    public Response deleteInterKey(@QueryParam("id") Long id) {
        int i = this.databaseProvider.getInterComTokenDao().deleteById(id);
        return new ResponseBuilder().success(i).build();
    }

}
