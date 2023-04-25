package com.example.netflixreposjava;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;

import static com.example.netflixreposjava.ResourceUtil.getBufferFromUrl;

@Path("/")
public class BaseResource {
    private static String base_cache;
    private static Timestamp timestamp;
    // cache data for every 500 seconds.
    private static final int update_period_in_ms = 500000;
    @GET
    @Produces("application/json")
    public String getBaseResource() throws IOException, NoSuchAlgorithmException, KeyManagementException {
        Timestamp current_ts = new Timestamp(System.currentTimeMillis());
        if (base_cache == null || timestamp == null || current_ts.getTime() - timestamp.getTime() > update_period_in_ms) {
            base_cache = getBufferFromUrl("https://api.github.com/");
            timestamp = current_ts;
        }
        return base_cache;
    }
}
