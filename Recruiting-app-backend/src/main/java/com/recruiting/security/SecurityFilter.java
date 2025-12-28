package com.recruiting.security;

import io.jsonwebtoken.Claims;
import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import jakarta.ws.rs.ext.Provider;
import jakarta.ws.rs.Priorities;

import java.io.IOException;
import java.security.Principal;

@Provider
@Priority(Priorities.AUTHENTICATION)
public class SecurityFilter implements ContainerRequestFilter {

    @Inject
    private TokenService tokenService;

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        String authHeader = requestContext.getHeaderString("Authorization");

        // Allow public access to auth endpoints and non-secured resources
        String path = requestContext.getUriInfo().getPath();
        if (path.startsWith("/auth") || (path.startsWith("/jobs") && requestContext.getMethod().equals("GET"))) {
            return;
        }

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
            return;
        }

        try {
            String token = authHeader.substring(7);
            Claims claims = tokenService.parseToken(token);
            String username = claims.getSubject();
            String role = claims.get("role", String.class);

            // Override SecurityContext
            requestContext.setSecurityContext(new SecurityContext() {
                @Override
                public Principal getUserPrincipal() {
                    return () -> username;
                }

                @Override
                public boolean isUserInRole(String r) {
                    return role != null && role.equals(r);
                }

                @Override
                public boolean isSecure() {
                    return requestContext.getUriInfo().getRequestUri().getScheme().equals("https");
                }

                @Override
                public String getAuthenticationScheme() {
                    return "Bearer";
                }
            });

        } catch (Exception e) {
            requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
        }
    }
}
