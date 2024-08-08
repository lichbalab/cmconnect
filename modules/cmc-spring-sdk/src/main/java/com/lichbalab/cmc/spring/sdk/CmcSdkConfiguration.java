package com.lichbalab.cmc.spring.sdk;

import com.lichbalab.cmc.sdk.CmcClientConfig;
import com.lichbalab.cmc.sdk.client.CmcClient;
import com.lichbalab.cmc.sdk.client.CmcClientFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@Configuration
public class CmcSdkConfiguration {

    @Bean
    public CmcClient cmcClient(CmcClientConfig config) {
        return CmcClientFactory.createService(config);
    }

    @Bean
    public TaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(1); // Set the pool size for the scheduler
        scheduler.setThreadNamePrefix("cmc-scheduled-task-pool-");
        scheduler.initialize();
        return scheduler;
    }
}