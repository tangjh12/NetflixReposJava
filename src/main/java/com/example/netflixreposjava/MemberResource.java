package com.example.netflixreposjava;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;

import javax.net.ssl.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;

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
            System.out.println("member: " + member);
            if (!member.equals("Netflix")) {
                stringBuilder.append(member).append("\n");
            }
            buffer = buffer.substring(index + 1);
            index = buffer.indexOf(mark);
        }
        return stringBuilder.toString();
    }
    String getBufferFromUrl(String url) throws IOException, NoSuchAlgorithmException, KeyManagementException {
        TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
            public java.security.cert.X509Certificate[] getAcceptedIssuers() { return null; }
            public void checkClientTrusted(X509Certificate[] certs, String authType) { }
            public void checkServerTrusted(X509Certificate[] certs, String authType) { }

        } };

        SSLContext sc = SSLContext.getInstance("SSL");
        sc.init(null, trustAllCerts, new java.security.SecureRandom());
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

        // Create all-trusting host name verifier
        HostnameVerifier allHostsValid = new HostnameVerifier() {
            public boolean verify(String hostname, SSLSession session) { return true; }
        };
        // Install the all-trusting host verifier
        HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
        /* End of the fix*/

        URL url1 = new URL(url);
        URLConnection con = url1.openConnection();
        StringBuilder stringBuilder = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"))) {
            for (String line; (line = reader.readLine()) != null;) {
                //System.out.println(line);
                stringBuilder.append(line);
            }
        }

        return stringBuilder.toString();
    }
}


