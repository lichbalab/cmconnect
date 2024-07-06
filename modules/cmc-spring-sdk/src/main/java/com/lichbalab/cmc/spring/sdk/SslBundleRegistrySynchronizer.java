package com.lichbalab.cmc.spring.sdk;

import com.lichbalab.cmc.sdk.CmcClientConfig;
import com.lichbalab.cmc.sdk.client.CmcClient;
import com.lichbalab.cmc.sdk.client.CmcClientFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ssl.SslBundle;
import org.springframework.boot.ssl.SslBundleRegistry;
import org.springframework.stereotype.Service;

@Service
public class SslBundleRegistrySynchronizer {

    private final SslBundleProvider sslBundleProvider;

    @Autowired
    public SslBundleRegistrySynchronizer(CmcClientConfig config) {
        CmcClient cmcClient = CmcClientFactory.createService(config);
        this.sslBundleProvider = new SslBundleProvider(cmcClient);
        CmcSslBundleRegistry sslBundleRegistry = CmcSslBundleRegistryProvider.getRegistry();
        SslBundle sslBundle = sslBundleProvider.getBundle();
        sslBundleRegistry.registerDefaultBundle(sslBundle);
    }

    public void synchronize() {
        CmcSslBundleRegistry sslBundleRegistry = CmcSslBundleRegistryProvider.getRegistry();
        SslBundle sslBundle = sslBundleProvider.getBundle();
        sslBundleRegistry.updateDefaultBundle(sslBundle);
    }
}