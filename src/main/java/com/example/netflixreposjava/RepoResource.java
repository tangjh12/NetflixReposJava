package com.example.netflixreposjava;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.util.*;

import static com.example.netflixreposjava.ResourceUtil.getBufferFromUrl;

@Path("/orgs/Netflix/repos")
public class RepoResource {
    private static List<NetflixRepo> repo_cache;
    private static String repo_str_cache;
    private static Timestamp timestamp;
    // update cache for every 300 seconds.
    private static final int update_period_in_ms = 300000;

    @GET
    @Produces("application/json")
    public String getRepoStr() throws IOException, NoSuchAlgorithmException, KeyManagementException {
        Timestamp current_ts = new Timestamp(System.currentTimeMillis());
        if (repo_str_cache == null || timestamp == null || current_ts.getTime() - timestamp.getTime() > update_period_in_ms) {
            repo_str_cache = String.join("", getBufferFromUrl("https://api.github.com/orgs/Netflix/repos"));
            timestamp = current_ts;
        }
        return repo_str_cache;
    }

}
