package de.lightfall.core.web.rest;

import com.j256.ormlite.stmt.Where;
import de.lightfall.core.common.DatabaseProvider;
import de.lightfall.core.common.models.StrikeModel;
import de.lightfall.core.common.models.TeamRecordModel;
import de.lightfall.core.common.models.UserInfoModel;
import de.lightfall.core.web.app.ResponseBuilder;
import de.lightfall.core.web.app.Secured;
import de.lightfall.core.web.entity.AdvancedUserInfoEntity;
import lombok.SneakyThrows;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.context.ImmutableContextSet;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.matcher.NodeMatcher;
import net.luckperms.api.node.types.InheritanceNode;
import net.luckperms.api.query.QueryOptions;
import org.bukkit.scoreboard.Team;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Stream;

@Path("/team/")
public class TeamService {

    private final LuckPerms luckPerms;
    private final ImmutableContextSet context;
    private final QueryOptions queryOptions;
    private final DatabaseProvider databaseProvider;

    public TeamService(LuckPerms luckPerms, DatabaseProvider databaseProvider) {
        this.luckPerms = luckPerms;
        this.databaseProvider = databaseProvider;
        this.context = luckPerms.getContextManager().getStaticContext();
        this.queryOptions = luckPerms.getContextManager().getStaticQueryOptions();
    }

    @SneakyThrows
    @GET
    @Secured
    @Produces({"application/json"})
    @Path("users")
    public Response getUsers() {
        Set<UUID> uuids = new HashSet<>();
        Map<String, Group> groupHashMap = new HashMap<>();
        Stream<Group> team = luckPerms.getGroupManager().getLoadedGroups().stream().filter(group -> group.getCachedData().getMetaData().getMetaValue("team") != null);
        team.forEach(group -> {
            try {
                groupHashMap.put(group.getName(), group);
                uuids.addAll(this.luckPerms.getUserManager().searchAll(NodeMatcher.key(InheritanceNode.builder()
                        .group(group).build())).get().keySet());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        });
        if (uuids.isEmpty()) return new ResponseBuilder().success(new ArrayList<>()).build();
        List<UserInfoModel> users = this.databaseProvider.getUserInfoDao().queryBuilder().where().in("uuid", uuids).query();
        List<AdvancedUserInfoEntity> advUserInfo = new ArrayList<>();
        for (UserInfoModel u : users) {
            User user = this.luckPerms.getUserManager().loadUser(u.getUuid()).get();
            int weight;
            try {
                weight = Integer.parseInt(Objects.requireNonNull(user.getCachedData().getMetaData().getMetaValue("weight")));
            } catch (Exception e) {
                weight = 0;
            }
            Group group = groupHashMap.get(user.getPrimaryGroup());
            advUserInfo.add(new AdvancedUserInfoEntity(u, group.getName(), group.getDisplayName(), weight));
        }
        advUserInfo.sort(Comparator.comparing(AdvancedUserInfoEntity::getWeight).reversed());
        return new ResponseBuilder().success(advUserInfo).build();
    }


    @SneakyThrows
    @PUT
    @Produces({MediaType.APPLICATION_JSON})
    @Path("record/{id}")
    public Response putRecord(@PathParam("id") Long id, @QueryParam("sender") Long senderId) {
        if (this.databaseProvider.getWebTeamRecordDao().queryBuilder().where().eq("userInfo_id", id).queryForFirst() != null)
            return new ResponseBuilder().error("Record already exists.").build();
        UserInfoModel userInfoModel = new UserInfoModel();
        userInfoModel.setId(id);
        UserInfoModel senderUserInfoModel = new UserInfoModel();
        senderUserInfoModel.setId(senderId);
        TeamRecordModel teamRecordModel = new TeamRecordModel(userInfoModel, senderUserInfoModel);
        return new ResponseBuilder().success(this.databaseProvider.getWebTeamRecordDao()
                .createIfNotExists(teamRecordModel)).build();
    }

    @SneakyThrows
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    @Path("record/{id}")
    public Response getRecord(@PathParam("id") Long id) {
        TeamRecordModel teamRecord = this.databaseProvider.getWebTeamRecordDao().queryBuilder().where().eq("userInfo_id", id).queryForFirst();
        if (teamRecord == null)
            return new ResponseBuilder().error("Record not exists.").build();
        return new ResponseBuilder().success(teamRecord).build();
    }

    @SneakyThrows
    @POST
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    @Path("record")
    public Response postRecord(TeamRecordModel record) {
        TeamRecordModel teamRecord = this.databaseProvider.getWebTeamRecordDao().queryForSameId(record);
        if (teamRecord == null)
            return new ResponseBuilder().error("Record not exists.").build();
        record.setUpdated_at(new Date());
        this.databaseProvider.getWebTeamRecordDao().update(record);
        return new ResponseBuilder().success(teamRecord).build();
    }

    @SneakyThrows
    @PUT
    @Path("strike")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public Response putStrike(StrikeModel strike, @QueryParam("length") Long length) {
        if (length != null && length >= 0)
            strike.setCustomEnd(Date.from(Instant.now().plusSeconds(length)));
        else
            strike.setCustomEnd(Date.from(Instant.now().plusSeconds((long) (strike.getTemplateModel().getBaseTime()*strike.getSeverity()))));
        return new ResponseBuilder().success(this.databaseProvider.getWebStrikeDao().createIfNotExists(strike)).build();
    }

    @SneakyThrows
    @Path("strikes")
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public Response getStrikes(@QueryParam("user") Long id) {
        List<StrikeModel> query = this.databaseProvider.getWebStrikeDao().queryBuilder().orderBy("created_at", true).where().eq("userInfo_id", id).query();
        return new ResponseBuilder().success(query).build();
    }

}