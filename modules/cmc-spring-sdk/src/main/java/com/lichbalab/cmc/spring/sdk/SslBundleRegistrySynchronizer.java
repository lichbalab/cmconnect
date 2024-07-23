package com.lichbalab.cmc.spring.sdk;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ssl.SslBundle;
import org.springframework.boot.ssl.SslBundleKey;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;

@Service
@DependsOn("cmcSslBundleRegistryInitializer")
public class SslBundleRegistrySynchronizer {

    private final SslBundleProvider sslBundleProvider;

    @Autowired
    public SslBundleRegistrySynchronizer(SslBundleProvider sslBundleProvider) {
        this.sslBundleProvider = sslBundleProvider;
    }

    public void synchronize() {
        CmcSslBundleRegistry sslBundleRegistry = CmcSslBundleRegistryProvider.getRegistry();
        SslBundle sslBundle = sslBundleProvider.getBundle();
        sslBundleRegistry.updateDefaultBundle(sslBundle);
    }

    public void synchronize(SslBundleKey key) {
        CmcSslBundleRegistry sslBundleRegistry = CmcSslBundleRegistryProvider.getRegistry();
        SslBundle sslBundle = sslBundleProvider.getBundle(key);
        sslBundleRegistry.updateDefaultBundle(sslBundle);
    }

    public void synchronize(String alias) {
        CmcSslBundleRegistry sslBundleRegistry = CmcSslBundleRegistryProvider.getRegistry();
        SslBundle sslBundle = sslBundleProvider.getBundle(SslBundleKey.of(null, alias));
        sslBundleRegistry.updateDefaultBundle(sslBundle);
    }
}