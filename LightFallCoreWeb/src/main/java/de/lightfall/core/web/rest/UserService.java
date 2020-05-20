package de.lightfall.core.web.rest;

import de.lightfall.core.common.DatabaseProvider;
import de.lightfall.core.models.UserInfoModel;
import de.lightfall.core.web.app.ResponseBuilder;
import de.lightfall.core.web.app.Secured;
import de.lightfall.core.web.entity.PermissionsMapEntity;
import lombok.SneakyThrows;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.context.ImmutableContextSet;
import net.luckperms.api.model.user.User;
import net.luckperms.api.query.QueryOptions;
import org.jboss.resteasy.plugins.providers.FormUrlEncodedProvider;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

@Path("/users/")
public class UserService {

    private final LuckPerms luckPerms;
    private final ImmutableContextSet context;
    private final QueryOptions queryOptions;
    private final DatabaseProvider databaseProvider;

    public UserService(LuckPerms luckPerms, DatabaseProvider databaseProvider) {
        this.luckPerms = luckPerms;
        this.databaseProvider = databaseProvider;
        this.context = luckPerms.getContextManager().getStaticContext();
        this.queryOptions = luckPerms.getContextManager().getStaticQueryOptions();
    }

    @GET
    @Secured
    @Produces({"application/json"})
    @Path("permissions/{uuid}")
    public Response getPermissions(@PathParam("uuid") String uuidString) {
        try {
            if (!uuidString.contains("-")) {
                uuidString = uuidString
                        .replaceFirst( "([0-9a-fA-F]{8})([0-9a-fA-F]{4})([0-9a-fA-F]{4})([0-9a-fA-F]{4})([0-9a-fA-F]+)", "$1-$2-$3-$4-$5" );
            }
            UUID uuid = UUID.fromString(uuidString);
            User user = luckPerms.getUserManager().loadUser(uuid).get();
            Map<String, Boolean> permissionMap = user.getCachedData().getPermissionData(queryOptions).getPermissionMap();
            return new ResponseBuilder().success(permissionMap).build();
        } catch (Exception ex) {
            return new ResponseBuilder().error("invalid uuid").build();
        }
    }

    @SneakyThrows
    @GET
    @Secured
    @Produces({MediaType.APPLICATION_JSON})
    @Path("{id}")
    public Response getUser(@PathParam("id") Long id, @QueryParam("type") String idType) {
        String column;
        System.out.println(idType);
        if (idType == null)
            return new ResponseBuilder().error("invalid type").build();
        switch (idType) {
            case "database":
                column = "id";
                break;
            case "forum":
                column = "forum_id";
                break;
            case "discord":
                column = "discord_id";
                break;
            case "teamspeak":
                column = "teamspeak_id";
                break;
            default:
                return new ResponseBuilder().error("invalid type").build();
        }
        UserInfoModel userInfoModel = this.databaseProvider.getUserInfoDao().queryBuilder().where().eq(column, id).queryForFirst();
        if (userInfoModel == null) return new ResponseBuilder().error("invalid id").build();
        return new ResponseBuilder().success(userInfoModel).getResponse();
    }


}
