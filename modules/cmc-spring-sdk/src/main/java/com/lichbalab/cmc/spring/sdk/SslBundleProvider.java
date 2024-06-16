package com.lichbalab.cmc.spring.sdk;

import com.lichbalab.cmc.sdk.client.CmcClient;
import org.springframework.boot.ssl.SslBundle;

public class SslBundleProvider {

    private final CmcClient cmcClient;

    public SslBundleProvider(CmcClient cmcClient) {
        this.cmcClient = cmcClient;
    }

    public SslBundle getBundle() {
        return null;
    }
}