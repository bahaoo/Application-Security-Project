package org.eclipse.jakarta.IAM.boundaries;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.jakarta.IAM.security.JwtManager;

@Path("/jwk")
@ApplicationScoped
public class JWKEndpoint {

    @Inject
    private JwtManager jwtManager;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPublicKey(@QueryParam("kid") String kid) {
        try {
            // Returns the public key JSON for a given key ID
            return Response.ok(jwtManager.getPublicValidationKey(kid).toJSONString())
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"" + e.getMessage() + "\"}")
                    .build();
        }
    }
}
