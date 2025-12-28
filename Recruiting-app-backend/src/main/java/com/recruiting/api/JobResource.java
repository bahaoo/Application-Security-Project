package com.recruiting.api;

import com.recruiting.entity.Job;
import com.recruiting.entity.User;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;

import java.time.LocalDateTime;
import java.util.List;

@Path("/jobs")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class JobResource {

    @PersistenceContext
    private EntityManager em;

    @GET
    public List<Job> getJobs() {
        return em.createQuery("SELECT j FROM Job j", Job.class).getResultList();
    }

    @POST
    @Transactional
    public Response createJob(Job job, @Context SecurityContext securityContext) {
        if (!securityContext.isUserInRole("RECRUITER")) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }

        // Ideally we fetch the user ID from the principal or token claims,
        // effectively binding the job to the logged-in recruiter.
        // For simplicity here, we assume the client might pass it or we'd extract it
        // from TokenService logic again if passed in context.
        // securityContext.getUserPrincipal().getName() gives email.

        String email = securityContext.getUserPrincipal().getName();
        User recruiter = em.createQuery("SELECT u FROM User u WHERE u.email = :email", User.class)
                .setParameter("email", email).getSingleResult();

        job.setRecruiterId(recruiter.getId());
        job.setCreatedAt(LocalDateTime.now());

        em.persist(job);

        return Response.status(Response.Status.CREATED).entity(job).build();
    }
}
