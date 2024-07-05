package com.lichbalab.cmc.spring.sdk;

import org.springframework.boot.ssl.SslBundles;

public class CmcSslBundleRegistryProvider {

    // The private static instance of the registry
    private static CmcDefaultSslBundleRegistry instance;

    // Private constructor to prevent instantiation
    private CmcSslBundleRegistryProvider() {
    }

    // Public method to get the instance
    private static synchronized CmcDefaultSslBundleRegistry getInstance() {
        if (instance == null) {
            instance = new CmcDefaultSslBundleRegistry();
        }
        return instance;
    }

    public static CmcSslBundleRegistry getRegistry() {
        return getInstance();
    }

    public static SslBundles getSslBundles() {
        return getInstance();
    }
}