package org.eclipse.jakarta.IAM.boundaries;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import org.eclipse.jakarta.IAM.controllers.IAMRepository;
import org.eclipse.jakarta.IAM.entities.Identity;
import org.eclipse.jakarta.IAM.security.AuthorizationCode;
import org.eclipse.jakarta.IAM.security.Argon2Utility;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Path("/")
@RequestScoped
public class AuthenticationEndpoint {

    public static final String AUTH_CONTEXT_COOKIE = "AUTH_CTX";

    @Inject
    IAMRepository iamRepository;

    /* =========================================================
       STEP 1 — OAuth Authorization Endpoint
       ========================================================= */
    @GET
    @Path("/authorize")
    @Produces(MediaType.TEXT_HTML)
    public Response authorize(@Context UriInfo uriInfo) {

        MultivaluedMap<String, String> params = uriInfo.getQueryParameters();

        String clientId = params.getFirst("client_id");
        String redirectUri = params.getFirst("redirect_uri");
        String responseType = params.getFirst("response_type");
        String scope = params.getFirst("scope");
        String codeChallenge = params.getFirst("code_challenge");

        if (clientId == null || redirectUri == null || responseType == null) {
            return error("Missing required OAuth parameters");
        }

        if (!"code".equals(responseType)) {
            return error("Only authorization_code flow is supported");
        }

        NewCookie contextCookie = new NewCookie.Builder(AUTH_CONTEXT_COOKIE)
                .value(clientId + "|" + redirectUri + "|" + scope + "|" + codeChallenge)
                .httpOnly(true)
                .sameSite(NewCookie.SameSite.STRICT)
                .path("/")
                .build();

        String loginForm = """
                <html>
                  <body>
                    <h2>Login</h2>
                    <form method="post" action="/login">
                      <input name="username" placeholder="Username"/><br/>
                      <input name="password" type="password" placeholder="Password"/><br/>
                      <button type="submit">Login</button>
                    </form>
                  </body>
                </html>
                """;

        return Response.ok(loginForm).cookie(contextCookie).build();
    }

    /* =========================================================
       STEP 2 — User Login Processing
       ========================================================= */
    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response login(@CookieParam(AUTH_CONTEXT_COOKIE) Cookie context,
                          @FormParam("username") String username,
                          @FormParam("password") String password) {

        if (context == null) {
            return error("Missing authorization context");
        }

        Optional<Identity> identityOpt = iamRepository.findIdentityByUsername(username);
        if (identityOpt.isEmpty()) {
            return error("Invalid credentials");
        }

        Identity identity = identityOpt.get();

        // 🔑 Use Argon2Utility exactly like in teacher's code
        if (!Argon2Utility.check(identity.getPassword(), password.toCharArray())) {
            return error("Invalid credentials");
        }

        String[] ctx = context.getValue().split("\\|");
        String clientId = ctx[0];
        String redirectUri = ctx[1];
        String scope = ctx.length > 2 ? ctx[2] : "";
        String codeChallenge = ctx.length > 3 ? ctx[3] : null;

        AuthorizationCode code = new AuthorizationCode(
                clientId,
                identity.getUsername(),
                scope,
                Instant.now().plus(2, ChronoUnit.MINUTES).getEpochSecond(),
                redirectUri
        );

        String redirect = redirectUri + "?code=" +
                URLEncoder.encode(code.getCode(codeChallenge), StandardCharsets.UTF_8);

        return Response.seeOther(URI.create(redirect))
                .cookie(expireContextCookie())
                .build();
    }

    /* =========================================================
       Utilities
       ========================================================= */
    private Response error(String message) {
        return Response.status(Response.Status.BAD_REQUEST)
                .entity("<h3>Error: " + message + "</h3>")
                .type(MediaType.TEXT_HTML)
                .build();
    }

    private NewCookie expireContextCookie() {
        return new NewCookie(AUTH_CONTEXT_COOKIE, "", "/", null, null, 0, false);
    }
}
