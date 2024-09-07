package com.lichbalab.cmc.spring.sdk;

import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.io.HttpClientConnectionManager;
import org.apache.hc.client5.http.ssl.NoopHostnameVerifier;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactoryBuilder;
import org.springframework.boot.ssl.SslBundle;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

public class CmcRestTemplateService implements CmcRestTemplate {

    private final CmcSslBundleRegistry registry;
    private final CmcSdkProperties properties;

    private RestTemplate restTemplate;

    public CmcRestTemplateService(CmcSdkProperties properties) {
        this.registry = CmcSslBundleRegistryProvider.getRegistry();
        this.properties = properties;
        this.restTemplate = createRestTemplate(registry.getDefaultBundle());
        CmcSslBundleRegistryProvider.getSslBundles().addBundleUpdateHandler(
                CmcDefaultSslBundleRegistry.SSL_BUNDLE_NAME,
                sslBundle -> updateSslBundle()
        );
    }

    public RestTemplate getRestTemplate() {
        return restTemplate;
    }

    public void updateSslBundle() {
        SslBundle newSslBundle = registry.getDefaultBundle();
        this.restTemplate = createRestTemplate(newSslBundle);
    }

    private RestTemplate createRestTemplate(SslBundle sslBundle) {
        SSLConnectionSocketFactoryBuilder factoryBuilder = SSLConnectionSocketFactoryBuilder.create()
                .setSslContext(sslBundle.createSslContext());

        if (Boolean.parseBoolean(properties.getDisableHostnameVerification())) {
            factoryBuilder.setHostnameVerifier(NoopHostnameVerifier.INSTANCE);
        }

        SSLConnectionSocketFactory sslSocketFactory = factoryBuilder.build();
        HttpClientConnectionManager cm = PoolingHttpClientConnectionManagerBuilder.create().setSSLSocketFactory(sslSocketFactory).build();
        HttpClient httpClient = HttpClients.custom().setConnectionManager(cm).evictExpiredConnections().build();
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory(httpClient);
        return new RestTemplate(factory);
    }
}