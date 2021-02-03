package de.cloud.core.web.app;

import de.cloud.core.common.DatabaseProvider;
import de.cloud.core.common.models.WebApiTokenModel;

import javax.annotation.Priority;
import javax.inject.Inject;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.Provider;
import java.io.IOException;

@Secured
@Provider
@Priority(1000)
public class AuthenticationFilter implements ContainerRequestFilter {
    private static final String REALM = "auth";

    @Inject
    private DatabaseProvider databaseProvider;

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        String authorizationHeader = requestContext.getHeaderString("Authorization");
        if (!this.isTokenBasedAuthentication(authorizationHeader)) {
            this.abortWithUnauthorized(requestContext);
        } else {
            String token = authorizationHeader.substring("Bearer".length()).trim();
            System.out.println(token);
            System.out.println(databaseProvider);
            try {
                this.validateToken(token, requestContext);
            } catch (Exception var5) {
                this.abortWithUnauthorized(requestContext);
            }

        }
    }

    private boolean isTokenBasedAuthentication(String authorizationHeader) {
        return authorizationHeader != null && authorizationHeader.toLowerCase().startsWith("Bearer".toLowerCase() + " ");
    }

    private void abortWithUnauthorized(ContainerRequestContext requestContext) {
        requestContext.abortWith(Response.status(Status.UNAUTHORIZED).header("WWW-Authenticate", "Bearer realm=\"" + REALM + "\"").build());
    }

    private void validateToken(String token, ContainerRequestContext context) throws Exception {
        WebApiTokenModel tokenModel = this.databaseProvider.getWebApiTokenDao().queryBuilder().where().eq("token", token).queryForFirst();
        if (tokenModel == null) throw new Exception();
    }
}

