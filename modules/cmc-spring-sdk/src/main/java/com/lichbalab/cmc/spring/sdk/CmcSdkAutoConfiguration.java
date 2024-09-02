package com.lichbalab.cmc.spring.sdk;

import com.lichbalab.cmc.sdk.CmcClientConfig;
import com.lichbalab.cmc.sdk.client.CmcClient;
import com.lichbalab.cmc.sdk.client.CmcClientFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@Configuration
public class CmcSdkAutoConfiguration {

    @Bean
    public CmcClientConfig cmcClientConfig(CmcSdkProperties properties) {
        CmcClientConfig config = new CmcClientConfig();
        config.setBaseUrl(properties.getBaseUrl());
        return config;
    }

    @Bean
    public CmcClient cmcClient(CmcClientConfig config) {
        return CmcClientFactory.createService(config);
    }

    @Bean
    @ConditionalOnMissingBean
    public CmcRestTemplate cmcRestTemplateService(CmcSdkProperties properties) {
        return new CmcRestTemplateService(properties);
    }

    @Bean
    public TaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(1); // Set the pool size for the scheduler
        scheduler.setThreadNamePrefix("cmc-scheduled-task-pool-");
        scheduler.initialize();
        return scheduler;
    }

    @Bean
    public SslBundleProvider sslBundleProvider(CmcClient cmcClient) {
        return new SslBundleProvider(cmcClient);
    }

    @Bean
    public SslBundleRegistrySynchronizer sslBundleRegistrySynchronizer(CmcSdkProperties properties, SslBundleProvider sslBundleProvider) {
        return new SslBundleRegistrySynchronizer(sslBundleProvider, properties);
    }

    @Bean
    public SslBundlesSynchronizationScheduler sslBundlesSynchronizationScheduler(SslBundleRegistrySynchronizer sslBundleRegistrySynchronizer, CmcSdkProperties cmcSdkProperties, TaskScheduler taskScheduler) {
        return new SslBundlesSynchronizationScheduler(sslBundleRegistrySynchronizer, cmcSdkProperties, taskScheduler);
    }

    @Bean
    @Lazy
    CmcSslBundleRegistryInitializer cmcSslBundleRegistryInitializer(SslBundleProvider sslBundleProvider, CmcSdkProperties properties) {
        return new CmcSslBundleRegistryInitializer(sslBundleProvider, properties);
    }

    @Bean
    @DependsOn("cmcSslBundleRegistryInitializer")
    @ConditionalOnMissingBean
    public CmcTomcatWebServerCustomizer cmcTomcatWebServerCustomizer(CmcSdkProperties properties) {
        return new CmcTomcatWebServerCustomizer(properties);
    }
}