package com.lichbalab.cmc.spring.sdk;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ssl.SslBundle;
import org.springframework.boot.ssl.SslBundleKey;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;

@Service
@DependsOn("cmcSslBundleRegistryInitializer")
public class SslBundleRegistrySynchronizer {

    private static final Logger log = LoggerFactory.getLogger(SslBundleRegistrySynchronizer.class);

    private final SslBundleProvider sslBundleProvider;
    private final CmcSdkProperties properties;

    @Autowired
    public SslBundleRegistrySynchronizer(SslBundleProvider sslBundleProvider, CmcSdkProperties properties) {
        this.sslBundleProvider = sslBundleProvider;
        this.properties = properties;
    }

    public void synchronize() {
        synchronize(properties.getSslBundleKeyAlias());
    }

    public void synchronize(SslBundleKey key) {
        CmcSslBundleRegistry sslBundleRegistry = CmcSslBundleRegistryProvider.getRegistry();
        SslBundle sslBundle = sslBundleProvider.getBundle(key);
        sslBundleRegistry.updateDefaultBundle(sslBundle);
        log.info("Synchronized SSL bundle with name: {}, key alias: {}", CmcDefaultSslBundleRegistry.SSL_BUNDLE_NAME, key.getAlias());
    }

    public void synchronize(String alias) {
        synchronize(SslBundleKey.of(null, alias));
    }
}