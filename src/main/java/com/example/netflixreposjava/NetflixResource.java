package com.example.netflixreposjava;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;

import static com.example.netflixreposjava.ResourceUtil.getBufferFromUrl;

@Path("/orgs/Netflix")
public class NetflixResource {
    private static String netflix_cache;
    private static Timestamp timestamp;
    // cache data for every 500 seconds.
    private static final int update_period_in_ms = 500000;
    @GET
    @Produces("application/json")
    public String getNetflixResource() throws IOException, NoSuchAlgorithmException, KeyManagementException {
        Timestamp current_ts = new Timestamp(System.currentTimeMillis());
        if (netflix_cache == null || timestamp == null || current_ts.getTime() - timestamp.getTime() > update_period_in_ms) {
            netflix_cache = getBufferFromUrl("https://api.github.com/orgs/Netflix").get(0);
            timestamp = current_ts;
        }
        return netflix_cache;
    }
}
