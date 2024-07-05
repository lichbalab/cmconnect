package com.lichbalab.cmc.spring.sdk;

import org.springframework.boot.ssl.SslBundleRegistry;

public interface CmcSslBundleRegistry extends SslBundleRegistry {

    void removeSslBundle(String key);
}
