package com.recruiting.api;

import com.recruiting.entity.Application;
import com.recruiting.entity.User;
import com.recruiting.entity.AccessLog;
import com.recruiting.watermark.WatermarkService;
import com.recruiting.watermark.WatermarkPayload;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import java.io.File;
import java.time.LocalDateTime;

@Path("/applications")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ApplicationResource {

    @PersistenceContext
    private EntityManager em;

    @Inject
    private WatermarkService watermarkService;

    @POST
    @Transactional
    public Response apply(Application application, @Context SecurityContext securityContext) {
        if (!securityContext.isUserInRole("CANDIDATE")) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }

        String email = securityContext.getUserPrincipal().getName();
        User candidate = em.createQuery("SELECT u FROM User u WHERE u.email = :email", User.class)
                .setParameter("email", email).getSingleResult();

        application.setCandidateId(candidate.getId());
        // cvPath should be handled by a file upload mechanism (Multipart),
        // but for this scope we assume the path is passed or already handled.

        em.persist(application);
        return Response.status(Response.Status.CREATED).entity(application).build();
    }

    @GET
    @Path("/{id}/cv")
    @Produces("application/pdf") // or image/png based on file
    @Transactional // To log access
    public Response downloadCV(@PathParam("id") Long id, @Context SecurityContext securityContext) {
        if (!securityContext.isUserInRole("RECRUITER")) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }

        Application application = em.find(Application.class, id);
        if (application == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        String email = securityContext.getUserPrincipal().getName();
        User recruiter = em.createQuery("SELECT u FROM User u WHERE u.email = :email", User.class)
                .setParameter("email", email).getSingleResult();

        // 1. Log Access
        AccessLog log = new AccessLog();
        log.setApplicationId(application.getId());
        log.setRecruiterId(recruiter.getId());
        log.setAccessType("DOWNLOAD_WATERMARKED");
        log.setTimestamp(LocalDateTime.now());
        em.persist(log);

        // 2. Prepare Watermark Payload
        WatermarkPayload payload = new WatermarkPayload();
        payload.setApplicationId(application.getId());
        payload.setCandidateId(application.getCandidateId());
        payload.setJobId(application.getJobId());
        payload.setRecruiterId(recruiter.getId()); // The recruiter DOWNLOADING it is marked
        payload.setTimestamp(LocalDateTime.now());
        payload.setUniqueHash(watermarkService.generateIntegrityHash(payload));

        // 3. Embed Watermark
        File originalFile = new File(application.getCvPath());

        // Check if file exists (mocking it if not for demo)
        if (!originalFile.exists()) {
            // For demo purposes, we might just fail or create a dummy file
            return Response.status(Response.Status.NOT_FOUND).entity("CV File missing on server").build();
        }

        try {
            File watermarkedFile = watermarkService.embedWatermark(originalFile, payload);
            return Response.ok(watermarkedFile)
                    .header("Content-Disposition", "attachment; filename=\"cv_secure.pdf\"")
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.serverError().entity("Watermarking failed").build();
        }
    }
}
