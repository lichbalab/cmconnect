package com.lichbalab.cmc.sdk;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.http.HttpResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class CertificateLoader {
    private final CloseableHttpClient httpClient;
    private final String cmcApiBaseUrl;

    public CertificateLoader(String cmcApiBaseUrl) {
        this.httpClient = HttpClients.createDefault();
        this.cmcApiBaseUrl = cmcApiBaseUrl;
    }

    public String loadCertificate(String alias) throws IOException {
        String encodedAlias = URLEncoder.encode(alias, StandardCharsets.UTF_8);
        String url = cmcApiBaseUrl + "/certificates/" + encodedAlias;

        HttpGet request = new HttpGet(url);
        request.addHeader("Accept", "application/json"); // Modify if the API does not return JSON

        HttpResponse response = httpClient.execute(request);
        int statusCode = response.getStatusLine().getStatusCode();

        if (statusCode == 200) {
            // Assuming the API returns the certificate directly in the response body
            String certificate = EntityUtils.toString(response.getEntity());
            return certificate; // Return the certificate string directly
        } else {
            throw new IOException("Failed to load certificate for alias " + alias + ": HTTP status " + statusCode);
        }
    }

    public void close() throws IOException {
        httpClient.close();
    }
}