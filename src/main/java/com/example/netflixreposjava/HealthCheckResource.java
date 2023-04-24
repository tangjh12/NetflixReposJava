package com.example.netflixreposjava;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Response;

@Path("/health-check")
public class HealthCheckResource {
    @GET
    @Produces("text/plain")
    public Response.Status healthCheck() {
        return Response.Status.fromStatusCode(200);
    }
}
