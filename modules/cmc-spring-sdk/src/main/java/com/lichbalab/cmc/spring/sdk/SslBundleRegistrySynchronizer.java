package com.lichbalab.cmc.spring.sdk;

import com.lichbalab.cmc.sdk.CmcClientConfig;
import com.lichbalab.cmc.sdk.client.CmcClient;
import com.lichbalab.cmc.sdk.client.CmcClientFactory;
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

}