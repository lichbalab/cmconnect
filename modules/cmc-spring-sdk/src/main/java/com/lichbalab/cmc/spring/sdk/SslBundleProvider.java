package com.lichbalab.cmc.spring.sdk;

import com.lichbalab.certificate.Certificate;
import com.lichbalab.cmc.sdk.client.CmcClient;
import org.springframework.boot.ssl.SslBundle;
import org.springframework.boot.ssl.SslStoreBundle;

import java.util.List;

public class SslBundleProvider {

    private final CmcClient cmcClient;

    public SslBundleProvider(CmcClient cmcClient) {
        this.cmcClient = cmcClient;
    }

    public SslBundle getBundle() {
        List<Certificate> cmcClientCertificates = cmcClient.getCertificates();
        SslStoreBundle sslStoreBundle = new CmcSslStoreBundle(cmcClientCertificates);
        return SslBundle.of(sslStoreBundle);
    }
}