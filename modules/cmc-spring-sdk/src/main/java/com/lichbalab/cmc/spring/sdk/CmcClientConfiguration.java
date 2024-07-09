package com.lichbalab.cmc.spring.sdk;

import com.lichbalab.cmc.sdk.CmcClientConfig;
import com.lichbalab.cmc.sdk.client.CmcClient;
import com.lichbalab.cmc.sdk.client.CmcClientFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CmcClientConfiguration {

    @Bean
    public CmcClient cmcClient(CmcClientConfig config) {
        return CmcClientFactory.createService(config);
    }
}