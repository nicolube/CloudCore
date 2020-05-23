package de.lightfall.core.web.rest;

import de.lightfall.core.common.DatabaseProvider;
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

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
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

}