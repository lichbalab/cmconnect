package com.lichbalab.cmc.spring.sdk;

import org.springframework.boot.ssl.SslBundle;
import org.springframework.boot.ssl.SslBundleRegistry;

public interface CmcSslBundleRegistry extends SslBundleRegistry {

    SslBundle getDefaultBundle();

    void registerDefaultBundle(SslBundle bundle);

    void updateDefaultBundle(SslBundle bundle);

    void removeSslBundle(String key);
}