package com.lichbalab.cmc.spring.sdk;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ssl.SslBundle;
import org.springframework.boot.ssl.SslBundleKey;
import org.springframework.stereotype.Component;

@Component("cmcSslBundleRegistryInitializer")
public class CmcSslBundleRegistryInitializer {

    private final SslBundleProvider sslBundleProvider;
    private final CmcSslBundleRegistry sslBundleRegistry;

    @Autowired
    public CmcSslBundleRegistryInitializer(SslBundleProvider sslBundleProvider) {
        this.sslBundleProvider = sslBundleProvider;
        this.sslBundleRegistry = CmcSslBundleRegistryProvider.getRegistry();
    }

    public void init() {
        SslBundle sslBundle = sslBundleProvider.getBundle();
        sslBundleRegistry.registerDefaultBundle(sslBundle);
    }

    public void init(SslBundleKey key) {
        SslBundle sslBundle = sslBundleProvider.getBundle(key);
        sslBundleRegistry.registerDefaultBundle(sslBundle);
    }

}