package com.lichbalab.cmc.spring.sdk;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.function.Supplier;

@Configuration
@ConfigurationProperties(prefix = "cmc.client")
public class CmcClientProperties {
    private String apiKey;
    private String baseUrl;
    private Supplier<String> baseUrlSupplier;

    // Getters and setters
    public String getApiKey() { return apiKey; }
    public void setApiKey(String apiKey) { this.apiKey = apiKey; }
    public String getBaseUrl() {
        if (baseUrlSupplier != null) {
            return baseUrlSupplier.get();
        }
        return baseUrl;
    }
    public void setBaseUrl(String baseUrl) { this.baseUrl = baseUrl; }

    public void setBaseUrlSupplier(Supplier<String> baseUrlSupplier) {
        this.baseUrlSupplier = baseUrlSupplier;
    }
}