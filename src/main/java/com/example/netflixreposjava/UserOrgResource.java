package com.example.netflixreposjava;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import static com.example.netflixreposjava.ResourceUtil.getBufferFromUrl;

@Path("/users")
public class UserOrgResource {
    private static final Map<String, String> user_org_cache = new HashMap<>();
    private static Timestamp timestamp;
    // cache data for every 500 seconds.
    private static final int update_period_in_ms = 500000;
    @GET
    @Produces("application/json")
    @Path("/{userName}/orgs")
    public String getUserOrgResource(@PathParam("userName") String user_name) throws IOException, NoSuchAlgorithmException, KeyManagementException {
        Timestamp current_ts = new Timestamp(System.currentTimeMillis());
        if (!user_org_cache.containsKey(user_name) || timestamp == null || current_ts.getTime() - timestamp.getTime() > update_period_in_ms) {
            user_org_cache.put(user_name, getBufferFromUrl("https://api.github.com/users/" + user_name + "/orgs").get(0));
            timestamp = current_ts;
        }
        return user_org_cache.get(user_name);
    }
}
