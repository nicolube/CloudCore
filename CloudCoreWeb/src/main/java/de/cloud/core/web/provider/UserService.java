package de.cloud.core.web.provider;

import com.google.inject.Inject;
import de.cloud.core.common.DatabaseProvider;
import de.cloud.core.common.models.UserInfoModel;
import de.cloud.core.web.app.ResponseBuilder;
import de.cloud.core.web.app.Secured;
import de.cloud.core.web.app.Util;
import lombok.SneakyThrows;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.context.ImmutableContextSet;
import net.luckperms.api.model.user.User;
import net.luckperms.api.query.QueryOptions;
import org.checkerframework.checker.nullness.qual.NonNull;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Map;

@Path("/users/")
public class UserService {

    @Inject
    private LuckPerms luckPerms;
    @Inject
    private ImmutableContextSet context;
    @Inject
    private QueryOptions queryOptions;
    @Inject
    private DatabaseProvider databaseProvider;


    @GET
    @Secured
    @Produces({"application/json"})
    @Path("permissions/{uuid}")
    public Response getPermissions(@PathParam("uuid") String uuid) {
        try {
            User user = luckPerms.getUserManager().loadUser(Util.uuiFromString(uuid)).get();
            Map<String, Boolean> permissionMap = user.getCachedData().getPermissionData().getPermissionMap();
            return new ResponseBuilder().success(permissionMap).build();
        } catch (Exception ex) {
            return new ResponseBuilder().error("invalid uuid").build();
        }
    }

    @GET
    @Secured
    @Produces({MediaType.APPLICATION_JSON})
    @Path("meta/{uuid}")
    public Response getMeta(@PathParam("uuid") String uuid) {
        try {
            User user = luckPerms.getUserManager().loadUser(Util.uuiFromString(uuid)).get();
            @NonNull Map<String, List<String>> metaMap = user.getCachedData().getMetaData().getMeta();
            return new ResponseBuilder().success(metaMap).build();
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
