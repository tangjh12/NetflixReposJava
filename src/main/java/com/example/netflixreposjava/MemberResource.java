package com.example.netflixreposjava;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.util.LinkedHashSet;

import static com.example.netflixreposjava.ResourceUtil.getBufferFromUrl;

@Path("/orgs/Netflix")
public class MemberResource {
    private static String people_cache;
    private static Timestamp timestamp;
    // cache data for every 500 seconds.
    private static final int update_period_in_ms = 500000;

    @Path("/members")
    @GET
    @Produces("application/json")
    public String getMemberResource() throws IOException, NoSuchAlgorithmException, KeyManagementException {
        Timestamp current_ts = new Timestamp(System.currentTimeMillis());
        if (people_cache == null || timestamp == null || current_ts.getTime() - timestamp.getTime() > update_period_in_ms) {
            people_cache = getBufferFromUrl("https://api.github.com/orgs/Netflix/members");
            timestamp = current_ts;
        }
        return people_cache;
    }

    LinkedHashSet<String> getMembers(String buffer) {
        LinkedHashSet<String> res = new LinkedHashSet<>();
        String mark = "alt=\"@";
        int index = buffer.indexOf(mark);
        while (index >= 0) {
            buffer = buffer.substring(index + mark.length());
            index = buffer.indexOf("\"");
            String member = buffer.substring(0, index);
            if (!member.equals("Netflix")) {
                res.add(member);
            }
            buffer = buffer.substring(index + 1);
            index = buffer.indexOf(mark);
        }
        return res;
    }
}


