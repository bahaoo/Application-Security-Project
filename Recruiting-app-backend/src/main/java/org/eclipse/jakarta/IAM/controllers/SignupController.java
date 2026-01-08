package org.eclipse.jakarta.IAM.controllers;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.jakarta.IAM.entities.Identity;
import org.eclipse.jakarta.IAM.controllers.IAMRepository;
import org.eclipse.jakarta.IAM.controllers.IAMIdentityStore;
import org.eclipse.jakarta.IAM.security.Argon2Utility;

import java.util.Optional;
import java.util.Objects;

@Path("/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SignupController {

    @Inject
    private IAMRepository iamRepository;

    @Inject
    private IAMIdentityStore identityStore;

    // ===== Sign up endpoint =====
    @POST
    @Path("/signup")
    public Response signup(SignupRequest request) {
        Objects.requireNonNull(request, "Signup request cannot be null");

        if (request.getUsername() == null || request.getPassword() == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Username and password are required").build();
        }

        Optional<Identity> existing = iamRepository.findIdentityByUsername(request.getUsername());
        if (existing.isPresent()) {
            return Response.status(Response.Status.CONFLICT)
                    .entity("Username already exists").build();
        }

        Identity identity = new Identity();
        identity.setUsername(request.getUsername());
        identity.setPassword(Argon2Utility.hash(request.getPassword().toCharArray()));
        identity.setProvidedScopes("profile.read cv.read cv.share"); // default scopes

        iamRepository.save(identity); // persist the new user

        return Response.status(Response.Status.CREATED)
                .entity("User registered successfully").build();
    }

    // ===== Login endpoint =====
    @POST
    @Path("/login")
    public Response login(LoginRequest request) {
        Objects.requireNonNull(request, "Login request cannot be null");

        if (request.getUsername() == null || request.getPassword() == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Username and password are required").build();
        }

        // Validate credentials using IAMIdentityStore
        boolean valid = identityStore.validateCredentials(request.getUsername(), request.getPassword());
        if (!valid) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("Invalid username or password").build();
        }

        return Response.ok("Login successful").build();
    }

    // ===== DTOs =====
    public static class SignupRequest {
        private String username;
        private String password;

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }

        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }

    public static class LoginRequest {
        private String username;
        private String password;

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }

        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }
}
