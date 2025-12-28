package com.recruiting.api;

import com.recruiting.watermark.WatermarkPayload;
import com.recruiting.watermark.WatermarkService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.io.File;

@Path("/forensics")
public class ForensicsResource {

    @Inject
    private WatermarkService watermarkService;

    @POST
    @Path("/extract")
    @Consumes(MediaType.APPLICATION_OCTET_STREAM)
    @Produces(MediaType.APPLICATION_JSON)
    public Response extractWatermark(File suspiciousFile) {
        // In a real scenario, we'd check for ADMIN role here.
        // @RolesAllowed("ADMIN")

        if (suspiciousFile == null || !suspiciousFile.exists()) {
            return Response.status(Response.Status.BAD_REQUEST).entity("No file provided").build();
        }

        try {
            WatermarkPayload payload = watermarkService.extractWatermark(suspiciousFile);
            return Response.ok(payload).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Extraction failed").build();
        }
    }
}
