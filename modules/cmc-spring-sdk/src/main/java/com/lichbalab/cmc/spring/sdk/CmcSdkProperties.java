package com.lichbalab.cmc.spring.sdk;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.function.Supplier;

/**
 * Configuration properties for the CMC SDK.
 * This class binds properties with the prefix "cmc.sdk" to its fields.
 */
@Configuration
@ConfigurationProperties(prefix = "cmc.sdk")
public class CmcSdkProperties {
    private String apiKey;
    private String baseUrl;
    private String sslBundleKeyAlias;
    private String clientAuth;
    private String synchronisationSchedulingCron = "0 0 0 * * *"; // Default value is midnight every day.
    private String disableHostnameVerification = "false";
    private Supplier<String> baseUrlSupplier;

    /**
     * Gets the API key.
     * @return the API key.
     */
    public String getApiKey() { return apiKey; }

    /**
     * Sets the API key.
     * @param apiKey the API key to set.
     */
    public void setApiKey(String apiKey) { this.apiKey = apiKey; }

    /**
     * Gets the base URL.
     * If a base URL supplier is set, it will use the supplier to get the base URL.
     * @return the base URL.
     */
    public String getBaseUrl() {
        if (baseUrlSupplier != null) {
            return baseUrlSupplier.get();
        }
        return baseUrl;
    }

    /**
     * Sets the base URL.
     * @param baseUrl the base URL to set.
     */
    public void setBaseUrl(String baseUrl) { this.baseUrl = baseUrl; }

    /**
     * Sets the base URL supplier.
     * @param baseUrlSupplier the supplier to set.
     */
    public void setBaseUrlSupplier(Supplier<String> baseUrlSupplier) {
        this.baseUrlSupplier = baseUrlSupplier;
    }

    /**
     * Gets the SSL bundle key alias.
     * @return the SSL bundle key alias.
     */
    public String getSslBundleKeyAlias() { return sslBundleKeyAlias; }

    /**
     * Sets the SSL bundle key alias.
     * @param sslBundleKeyAlias the SSL bundle key alias to set.
     */
    public void setSslBundleKeyAlias(String sslBundleKeyAlias) {
        this.sslBundleKeyAlias = sslBundleKeyAlias;
    }

    /**
     * Gets the client authentication type.
     * @return the client authentication type.
     */
    public String getClientAuth() { return clientAuth; }

    /**
     * Sets the client authentication type.
     * @param clientAuth the client authentication type to set.
     */
    public void setClientAuth(String clientAuth) { this.clientAuth = clientAuth; }

    /**
     * Gets the synchronization scheduling cron expression.
     * @return the synchronization scheduling cron expression.
     */
    public String getSynchronisationSchedulingCron() { return synchronisationSchedulingCron; }

    /**
     * Sets the synchronization scheduling cron expression.
     * @param synchronisationSchedulingCron the cron expression to set.
     */
    public void setSynchronisationSchedulingCron(String synchronisationSchedulingCron) {
        this.synchronisationSchedulingCron = synchronisationSchedulingCron;
    }

    /**
     * Gets the flag to disable hostname verification.
     * @return the flag to disable hostname verification.
     */
    public String getDisableHostnameVerification() { return disableHostnameVerification; }

    /**
     * Sets the flag to disable hostname verification.
     * @param disableHostnameVerification the flag to set.
     */
    public void setDisableHostnameVerification(String disableHostnameVerification) {
        this.disableHostnameVerification = disableHostnameVerification;
    }
}