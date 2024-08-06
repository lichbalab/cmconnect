package com.lichbalab.cmc.spring.sdk;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class SslBundlesSynchronizationScheduler {

    private static final Logger log = LoggerFactory.getLogger(SslBundlesSynchronizationScheduler.class);

    private final SslBundleRegistrySynchronizer sslBundleRegistrySynchronizer;

    @Autowired
    public SslBundlesSynchronizationScheduler(SslBundleRegistrySynchronizer sslBundleRegistrySynchronizer, CmcSslBundleRegistryInitializer sslBundleRegistryInitializer) {
        this.sslBundleRegistrySynchronizer = sslBundleRegistrySynchronizer;
        log.info("SSL Bundles Synchronization Scheduler started.");
    }

    @Scheduled(cron = "${cmc.synchronisation.scheduling.cron:0 0 0 * * *}")
    public void synchronizeSslBundles() {
        sslBundleRegistrySynchronizer.synchronize();
        log.info("Synchronized SSL bundles on schedule.");
    }
}