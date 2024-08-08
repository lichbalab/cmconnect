package com.lichbalab.cmc.spring.sdk;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.function.Supplier;

@Configuration
@ConfigurationProperties(prefix = "cmc.sdk")
public class CmcSdkProperties {
    private String apiKey;
    private String baseUrl;
    private String sslBundleKeyAlias;
    private String clientAuth;
    private String synchronisationSchedulingCron;
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

    public String getSslBundleKeyAlias() { return sslBundleKeyAlias; }

    public void setSslBundleKeyAlias(String sslBundleKeyAlias) {
        this.sslBundleKeyAlias = sslBundleKeyAlias;
    }

    public String getClientAuth() { return clientAuth; }

    public void setClientAuth(String clientAuth) { this.clientAuth = clientAuth; }

    public String getSynchronisationSchedulingCron() { return synchronisationSchedulingCron; }

    public void setSynchronisationSchedulingCron(String synchronisationSchedulingCron) {
        this.synchronisationSchedulingCron = synchronisationSchedulingCron;
    }
}