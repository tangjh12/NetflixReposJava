package com.example.netflixreposjava;

import javax.net.ssl.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ResourceUtil {
    public static List<String> getBufferFromUrl(String url) throws IOException, NoSuchAlgorithmException, KeyManagementException {
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

        List<String> res = new ArrayList<>();

        while (true) {
            StringBuilder stringBuilder = new StringBuilder();
            URL url1 = new URL(url);
            URLConnection con = url1.openConnection();

            // pass GITHUB_API_TOKEN in request header.
            Map<String, String> env_map = System.getenv();
            if (env_map.containsKey("GITHUB_API_TOKEN")) {
                con.setRequestProperty("Authorization", "Bearer " + env_map.get("GITHUB_API_TOKEN"));
            }

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8))) {
                for (String line; (line = reader.readLine()) != null;) {
                    stringBuilder.append(line);
                }
            }
            res.add(stringBuilder.toString());

            // Curl next page.
            String link_value = con.getHeaderField("Link");
            if (link_value == null || !link_value.contains("next")) {
                break;
            }
            int prev = link_value.indexOf("prev");
            if (prev >= 0) {
                link_value = link_value.substring(prev);
            }
            int begin = link_value.indexOf('<'), end = link_value.indexOf('>');
            url = link_value.substring(begin + 1, end);
        }

        return res;
    }

}
