package org.eclipse.jakarta.IAM.boundaries;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.jakarta.IAM.controllers.IAMRepository;
import org.eclipse.jakarta.IAM.security.AuthorizationCode;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Set;
import java.util.UUID;

@Path("/oauth/token")
public class TokenEndpoint {

    private final Set<String> supportedGrantTypes = Set.of("authorization_code", "refresh_token");

    @Inject
    private IAMRepository iamRepository;

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response token(@FormParam("grant_type") String grantType,
                          @FormParam("code") String authCode,
                          @FormParam("code_verifier") String codeVerifier) {

        if (grantType == null || grantType.isEmpty()) {
            return responseError("invalid_request", "grant_type is required", Response.Status.BAD_REQUEST);
        }

        if (!supportedGrantTypes.contains(grantType)) {
            return responseError("unsupported_grant_type",
                    "grant_type should be one of: " + supportedGrantTypes, Response.Status.BAD_REQUEST);
        }

        try {
            if ("refresh_token".equals(grantType)) {
                // Simplified dummy refresh token flow
                String dummyAccessToken = "access-" + UUID.randomUUID();
                String dummyRefreshToken = "refresh-" + UUID.randomUUID();

                JsonObject response = Json.createObjectBuilder()
                        .add("token_type", "Bearer")
                        .add("access_token", dummyAccessToken)
                        .add("expires_in", 3600)
                        .add("scope", "demo_scope")
                        .add("refresh_token", dummyRefreshToken)
                        .build();
                return Response.ok(response)
                        .header("Cache-Control", "no-store")
                        .header("Pragma", "no-cache")
                        .build();
            }

            // Authorization code flow
            AuthorizationCode decoded = AuthorizationCode.decode(authCode, codeVerifier);
            if (decoded == null) {
                return responseError("invalid_grant", "Invalid authorization code", Response.Status.BAD_REQUEST);
            }

            // Generate dummy tokens
            String dummyAccessToken = "access-" + UUID.randomUUID();
            String dummyRefreshToken = "refresh-" + UUID.randomUUID();

            JsonObject response = Json.createObjectBuilder()
                    .add("token_type", "Bearer")
                    .add("access_token", dummyAccessToken)
                    .add("expires_in", 3600)
                    .add("scope", decoded.approvedScopes())
                    .add("refresh_token", dummyRefreshToken)
                    .build();

            return Response.ok(response)
                    .header("Cache-Control", "no-store")
                    .header("Pragma", "no-cache")
                    .build();

        } catch (Exception e) {
            return responseError("server_error", "Unable to process token request", Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    private Response responseError(String error, String errorDescription, Response.Status status) {
        JsonObject errorResponse = Json.createObjectBuilder()
                .add("error", error)
                .add("error_description", errorDescription)
                .build();
        return Response.status(status).entity(errorResponse).build();
    }
}
