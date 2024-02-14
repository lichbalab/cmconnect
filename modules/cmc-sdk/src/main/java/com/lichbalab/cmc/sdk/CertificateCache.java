package com.lichbalab.cmc.sdk;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.lichbalab.certificate.Certificate;

public class CertificateCache {
    private final Map<String, Certificate> cache = new ConcurrentHashMap<>();

    public void addCertificate(String alias, Certificate certificate) {
        cache.put(alias, certificate);
    }

    public Certificate getCertificate(String alias) {
        return cache.get(alias);
    }

    public boolean hasCertificate(String alias) {
        return cache.containsKey(alias);
    }
}