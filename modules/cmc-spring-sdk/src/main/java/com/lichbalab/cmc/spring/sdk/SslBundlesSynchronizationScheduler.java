package com.lichbalab.cmc.spring.sdk;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

import java.util.concurrent.ScheduledFuture;

@Component
public class SslBundlesSynchronizationScheduler {

    private static final Logger log = LoggerFactory.getLogger(SslBundlesSynchronizationScheduler.class);

    private final SslBundleRegistrySynchronizer sslBundleRegistrySynchronizer;
    private final CmcSdkProperties cmcSdkProperties;

    @Autowired
    public SslBundlesSynchronizationScheduler(SslBundleRegistrySynchronizer sslBundleRegistrySynchronizer, CmcSdkProperties cmcSdkProperties) {
        this.sslBundleRegistrySynchronizer = sslBundleRegistrySynchronizer;
        this.cmcSdkProperties = cmcSdkProperties;
        log.info("SSL Bundles Synchronization Scheduler started.");
    }

    @Autowired
    private TaskScheduler taskScheduler;

    private ScheduledFuture<?> futureTask;

    @PostConstruct
    public void scheduleTask() {
        futureTask = taskScheduler.schedule(
            sslBundleRegistrySynchronizer::synchronize,
                new CronTrigger(cmcSdkProperties.getSynchronisationSchedulingCron())
        );
    }

    @PreDestroy
    public void onShutdown() {
        stopTask();
    }

    public void stopTask() {
        if (futureTask != null) {
            futureTask.cancel(false);
        }
    }


/*    @Scheduled(cron = "${cmc.sdk.synchronisationSchedulingCron}")
    public void synchronizeSslBundles() {
        sslBundleRegistrySynchronizer.synchronize();
        log.info("Synchronized SSL bundles on schedule.");
    }*/
}