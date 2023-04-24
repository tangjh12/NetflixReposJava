package com.example.netflixreposjava;

import javax.net.ssl.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.Base64;
import java.util.Map;

public class ResourceUtil {
    public static String getBufferFromUrl(String url) throws IOException, NoSuchAlgorithmException, KeyManagementException {
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
        // pass GITHUB_API_TOKEN in request header
        Map<String, String> env_map = System.getenv();
        if (env_map.containsKey("GITHUB_API_TOKEN")) {
            String authorization = env_map.get("GITHUB_API_TOKEN");
            System.out.println("github api token: " + authorization);
            byte[] encodedBytes = Base64.getEncoder().encode(authorization.getBytes());
            authorization = "token " + new String(encodedBytes);
            con.setRequestProperty("Authorization", authorization);
        }

        StringBuilder stringBuilder = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"))) {
            for (String line; (line = reader.readLine()) != null;) {
                stringBuilder.append(line);
            }
        }

        return stringBuilder.toString();
    }

}
