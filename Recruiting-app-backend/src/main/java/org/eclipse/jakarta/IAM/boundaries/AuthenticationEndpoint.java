package org.eclipse.jakarta.IAM.boundaries;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;

import org.eclipse.jakarta.IAM.controllers.IAMRepository;
import org.eclipse.jakarta.IAM.entities.Grant;
import org.eclipse.jakarta.IAM.entities.Identity;
import org.eclipse.jakarta.IAM.entities.Tenant;
import org.eclipse.jakarta.IAM.security.Argon2Utility;
import org.eclipse.jakarta.IAM.security.AuthorizationCode;

import java.io.InputStream;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Path("/")
@RequestScoped
public class AuthenticationEndpoint {

    public static final String AUTH_CONTEXT_COOKIE = "authCtx";

    @Inject
    private IAMRepository iamRepository;

    // ===== Step 1: Show login form =====
    @GET
    @Produces(MediaType.TEXT_HTML)
    @Path("/authorize")
    public Response authorize(@Context UriInfo uriInfo) {
        MultivaluedMap<String, String> params = uriInfo.getQueryParameters();
        String clientId = params.getFirst("client_id");
        String redirectUri = params.getFirst("redirect_uri");
        String requestedScope = params.getFirst("scope");

        if (clientId == null || redirectUri == null) {
            return error("Missing client_id or redirect_uri");
        }

        Optional<Tenant> tenantOpt = iamRepository.findTenantByName(clientId);
        if (tenantOpt.isEmpty()) {
            return error("Invalid client_id");
        }
        Tenant tenant = tenantOpt.get();

        try {
            StreamingOutput stream = output -> {
                try (InputStream is = Objects.requireNonNull(getClass().getResource("/login.html")).openStream()) {
                    output.write(is.readAllBytes());
                }
            };

            // Save authorization context in cookie for POST login
            String ctxValue = clientId + "|" + redirectUri + "|" + (requestedScope != null ? requestedScope : "");
            NewCookie cookie = new NewCookie.Builder(AUTH_CONTEXT_COOKIE)
                    .httpOnly(true).secure(true).sameSite(NewCookie.SameSite.STRICT)
                    .value(ctxValue).build();

            return Response.ok(stream)
                    .cookie(cookie)
                    .build();

        } catch (Exception e) {
            return error("Failed to load login page: " + e.getMessage());
        }
    }

    // ===== Step 2: Handle login submission =====
    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response login(@CookieParam(AUTH_CONTEXT_COOKIE) Cookie context,
                          @FormParam("username") String username,
                          @FormParam("password") String password) {

        if (context == null) return error("Missing authorization context");

        Optional<Identity> identityOpt = iamRepository.findIdentityByUsername(username);
        if (identityOpt.isEmpty()) return error("Invalid credentials");

        Identity identity = identityOpt.get();
        if (!Argon2Utility.check(identity.getPassword(), password.toCharArray())) {
            return error("Invalid credentials");
        }

        // Parse cookie context
        String[] ctx = context.getValue().split("\\|");
        String clientId = ctx[0];
        String redirectUri = ctx[1];
        String scope = ctx.length > 2 ? ctx[2] : "";

        try {
            AuthorizationCode code = new AuthorizationCode(
                    clientId,
                    identity.getUsername(),
                    scope,
                    Instant.now().plus(2, ChronoUnit.MINUTES).getEpochSecond(),
                    redirectUri
            );

            String redirect = redirectUri + "?code=" +
                    URLEncoder.encode(code.getCode(null), StandardCharsets.UTF_8);

            return Response.seeOther(URI.create(redirect))
                    .cookie(expireContextCookie())
                    .build();

        } catch (Exception e) {
            return error("Failed to generate authorization code: " + e.getMessage());
        }
    }

    // ===== Helper methods =====
    private Response error(String message) {
        String html = """
                <!DOCTYPE html>
                <html>
                <head><meta charset="UTF-8"/><title>Error</title></head>
                <body>
                    <p>%s</p>
                </body>
                </html>
                """.formatted(message);
        return Response.status(Response.Status.BAD_REQUEST)
                .entity(html).build();
    }

    private NewCookie expireContextCookie() {
        return new NewCookie(AUTH_CONTEXT_COOKIE, "", "/", null, "", 0, false, true);
    }
}
