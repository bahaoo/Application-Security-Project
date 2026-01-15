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
import org.eclipse.jakarta.IAM.entities.Identity;
import org.eclipse.jakarta.IAM.entities.Tenant;
import org.eclipse.jakarta.IAM.security.AuthorizationCode;
import org.eclipse.jakarta.IAM.security.JwtManager;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import java.util.Optional;
import java.util.Set;

@Path("/oauth/token")
public class TokenEndpoint {

    private final Set<String> supportedGrantTypes = Set.of("authorization_code", "refresh_token");

    @Inject
    private IAMRepository iamRepository;

    @Inject
    private JwtManager jwtManager;

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response token(@FormParam("grant_type") String grantType,
            @FormParam("code") String authCode,
            @FormParam("code_verifier") String codeVerifier,
            @FormParam("client_id") String clientId,
            @FormParam("client_secret") String clientSecret,
            @FormParam("refresh_token") String refreshToken) {

        if (grantType == null || grantType.isEmpty()) {
            return responseError("invalid_request", "grant_type is required", Response.Status.BAD_REQUEST);
        }

        if (!supportedGrantTypes.contains(grantType)) {
            return responseError("unsupported_grant_type",
                    "grant_type should be one of: " + supportedGrantTypes, Response.Status.BAD_REQUEST);
        }

        try {
            // Validate client
            Optional<Tenant> tenantOpt = iamRepository.findTenantByClientId(clientId);
            if (tenantOpt.isEmpty()) {
                return responseError("invalid_client", "Unknown client", Response.Status.UNAUTHORIZED);
            }
            Tenant tenant = tenantOpt.get();

            if ("refresh_token".equals(grantType)) {
                return handleRefreshToken(refreshToken, tenant);
            }

            // Authorization code flow
            AuthorizationCode decoded = AuthorizationCode.decode(authCode, codeVerifier);
            if (decoded == null) {
                return responseError("invalid_grant", "Invalid authorization code or code_verifier",
                        Response.Status.BAD_REQUEST);
            }

            // Check expiration
            if (decoded.expirationDate() < System.currentTimeMillis() / 1000) {
                return responseError("invalid_grant", "Authorization code has expired", Response.Status.BAD_REQUEST);
            }

            // Find identity
            Optional<Identity> identityOpt = iamRepository.findIdentityByUsername(decoded.identityUsername());
            if (identityOpt.isEmpty()) {
                return responseError("invalid_grant", "User not found", Response.Status.BAD_REQUEST);
            }
            Identity identity = identityOpt.get();

            // Generate real JWT tokens
            String accessToken = jwtManager.generateAccessToken(
                    tenant.getClientId(),
                    identity.getUsername(),
                    decoded.approvedScopes(),
                    new String[] { "USER" });
            String newRefreshToken = jwtManager.generateRefreshToken(
                    tenant.getClientId(),
                    identity.getUsername(),
                    decoded.approvedScopes());

            JsonObject response = Json.createObjectBuilder()
                    .add("token_type", "Bearer")
                    .add("access_token", accessToken)
                    .add("expires_in", 3600)
                    .add("scope", decoded.approvedScopes())
                    .add("refresh_token", newRefreshToken)
                    .build();

            return Response.ok(response)
                    .header("Cache-Control", "no-store")
                    .header("Pragma", "no-cache")
                    .build();

        } catch (Exception e) {
            return responseError("server_error", "Unable to process token request: " + e.getMessage(),
                    Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    private Response handleRefreshToken(String refreshToken, Tenant tenant) {
        try {
            // Validate the refresh token
            var jwtOpt = jwtManager.validateJWT(refreshToken);
            if (jwtOpt.isEmpty()) {
                return responseError("invalid_grant", "Invalid or expired refresh token", Response.Status.BAD_REQUEST);
            }

            var jwt = jwtOpt.get();
            String subject = jwt.getJWTClaimsSet().getSubject();
            String scope = (String) jwt.getJWTClaimsSet().getClaim("scope");

            // Generate new tokens
            String newAccessToken = jwtManager.generateAccessToken(
                    tenant.getClientId(),
                    subject,
                    scope,
                    new String[] { "USER" });
            String newRefreshToken = jwtManager.generateRefreshToken(
                    tenant.getClientId(),
                    subject,
                    scope);

            JsonObject response = Json.createObjectBuilder()
                    .add("token_type", "Bearer")
                    .add("access_token", newAccessToken)
                    .add("expires_in", 3600)
                    .add("scope", scope != null ? scope : "")
                    .add("refresh_token", newRefreshToken)
                    .build();

            return Response.ok(response)
                    .header("Cache-Control", "no-store")
                    .header("Pragma", "no-cache")
                    .build();
        } catch (Exception e) {
            return responseError("invalid_grant", "Failed to refresh token", Response.Status.BAD_REQUEST);
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
