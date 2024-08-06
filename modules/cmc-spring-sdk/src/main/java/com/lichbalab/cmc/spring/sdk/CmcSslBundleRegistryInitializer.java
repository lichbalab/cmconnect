package com.lichbalab.cmc.spring.sdk;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ssl.SslBundle;
import org.springframework.boot.ssl.SslBundleKey;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.ApplicationScope;

@Component("cmcSslBundleRegistryInitializer")
public class CmcSslBundleRegistryInitializer {

    private final SslBundleProvider sslBundleProvider;
    private final CmcSslBundleRegistry sslBundleRegistry;
    private final CmcSdkProperties properties;

    @Autowired
    public CmcSslBundleRegistryInitializer(SslBundleProvider sslBundleProvider, CmcSdkProperties properties) {
        this.sslBundleProvider = sslBundleProvider;
        this.sslBundleRegistry = CmcSslBundleRegistryProvider.getRegistry();
        this.properties = properties;
        init();
    }

    public void init() {
        init(SslBundleKey.of(null, properties.getSslBundleKeyAlias()));
    }

    public void init(SslBundleKey key) {
        SslBundle sslBundle = sslBundleProvider.getBundle(key);
        sslBundleRegistry.registerDefaultBundle(sslBundle);
    }

}