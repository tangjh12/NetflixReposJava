package com.example.netflixreposjava;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import static com.example.netflixreposjava.ResourceUtil.getBufferFromUrl;

@Path("/orgs/Netflix/members")
public class MemberResource {
    @GET
    @Produces("text/plain")
    public String getPeopleBuffer() throws IOException, NoSuchAlgorithmException, KeyManagementException {
        String github_url = "https://github.com";
        String people_endpoint  = "/orgs/Netflix/people";
        String people_buffer = getBufferFromUrl(github_url + people_endpoint);
        return getMembers(people_buffer);
    }

    String getMembers(String buffer) {
        StringBuilder stringBuilder = new StringBuilder();
        String mark = "alt=\"@";
        int index = buffer.indexOf(mark);
        while (index >= 0) {
            buffer = buffer.substring(index + mark.length());
            index = buffer.indexOf("\"");
            String member = buffer.substring(0, index);
            if (!member.equals("Netflix")) {
                stringBuilder.append(member).append("\n");
            }
            buffer = buffer.substring(index + 1);
            index = buffer.indexOf(mark);
        }
        return stringBuilder.toString();
    }
}


