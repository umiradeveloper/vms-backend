package org.sim.umira.jwt;

import java.io.IOException;
import java.security.Principal;
import java.util.Arrays;
import java.util.List;

import org.sim.umira.handlers.ResponseHandler;

import io.jsonwebtoken.JwtException;
import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import jakarta.ws.rs.ext.Provider;


@Provider
@Secured
@Priority(Priorities.AUTHENTICATION)
public class JwtFilter implements ContainerRequestFilter {
    @Inject
    JwtService js;
    private static final List<String> EXCLUDED_PATHS = Arrays.asList(
            "/auth/login",
            "/auth/register",
            "/menu/"
    );

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        
        String path = requestContext.getUriInfo().getPath();
        if (isExcluded(path)) {
                return; // lewati filter
        }
        String authHeader = requestContext.getHeaderString("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer")) {
            abort(requestContext, "Missing or invalid Authorization header");
            return;
        }

        String token = authHeader.substring("Bearer".length()).trim();

        try {
           String subject = js.validateToken(token);

            // inject subject ke context kalau perlu
            // requestContext.setProperty("user", subject);
            requestContext.setSecurityContext(new SecurityContext() {
                @Override
                public Principal getUserPrincipal(){
                    return () -> subject;
                }
                @Override
                public boolean isUserInRole(String role) {
                    // bisa cek claim "roles" dari JWT di sini
                    return role != null && role.contains(role);
                }

                @Override
                public boolean isSecure() {
                    return requestContext.getUriInfo()
                                        .getAbsolutePath()
                                        .getScheme()
                                        .equals("https");
                }

                @Override
                public String getAuthenticationScheme() {
                    return "JWT";
                }
            });

        } catch (JwtException e) {
            abort(requestContext, "Invalid or expired token");
        }
    }

    private void abort(ContainerRequestContext requestContext, String message) {
        requestContext.abortWith(
                Response.status(Response.Status.UNAUTHORIZED)
                        .entity(ResponseHandler.error("Unauthorized"))
                        .build()
        );
    }
    private boolean isExcluded(String path) {
        return EXCLUDED_PATHS.stream().anyMatch(path::startsWith);
    }
}
