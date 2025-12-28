package com.recruiting.api;

import com.recruiting.entity.User;
import com.recruiting.security.PasswordUtils;
import com.recruiting.security.TokenService;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthResource {

    @PersistenceContext(unitName = "primary") // Assuming default unit name
    private EntityManager em;

    @Inject
    private TokenService tokenService;

    @POST
    @Path("/register")
    @Transactional
    public Response register(User user) {
        if (user.getEmail() == null || user.getPasswordHash() == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Invalid data").build();
        }

        // Hash password
        user.setPasswordHash(PasswordUtils.hashPassword(user.getPasswordHash()));

        try {
            em.persist(user);
        } catch (Exception e) {
            return Response.status(Response.Status.CONFLICT).entity("Email already exists").build();
        }

        return Response.status(Response.Status.CREATED).entity("User registered").build();
    }

    @POST
    @Path("/login")
    public Response login(User credentials) {
        try {
            User user = em.createQuery("SELECT u FROM User u WHERE u.email = :email", User.class)
                    .setParameter("email", credentials.getEmail())
                    .getSingleResult();

            if (user != null && PasswordUtils.checkPassword(credentials.getPasswordHash(), user.getPasswordHash())) {
                String token = tokenService.generateToken(user.getEmail(), user.getRole(), user.getId());
                return Response.ok("{\"token\":\"" + token + "\"}").build();
            }
        } catch (Exception e) {
            // User not found
        }
        return Response.status(Response.Status.UNAUTHORIZED).entity("Invalid credentials").build();
    }
}
