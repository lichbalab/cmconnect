package com.lichbalab.cmc.spring.sdk;

import com.lichbalab.cmc.sdk.CmcClientConfig;
import com.lichbalab.cmc.sdk.client.CmcClient;
import com.lichbalab.cmc.sdk.client.CmcClientFactory;
import org.springframework.boot.ssl.SslBundle;
import org.springframework.boot.ssl.SslBundleRegistry;

public class SslBundleRegistrySynchronizer {

    private static final String SSL_BUNDLE_NAME = "default";

    private final SslBundleProvider sslBundleProvider;

    public SslBundleRegistrySynchronizer(CmcClientConfig config) {
        CmcClient cmcClient = CmcClientFactory.createService(config);
        this.sslBundleProvider = new SslBundleProvider(cmcClient);
        SslBundleRegistry sslBundleRegistry = CmcSslBundleRegistryProvider.getRegistry();
        SslBundle sslBundle = sslBundleProvider.getBundle();
        sslBundleRegistry.registerBundle(SSL_BUNDLE_NAME, sslBundle);
    }

    public void synchronize() {
        SslBundleRegistry sslBundleRegistry = CmcSslBundleRegistryProvider.getRegistry();
        SslBundle sslBundle = sslBundleProvider.getBundle();
        sslBundleRegistry.updateBundle(SSL_BUNDLE_NAME, sslBundle);
    }
}