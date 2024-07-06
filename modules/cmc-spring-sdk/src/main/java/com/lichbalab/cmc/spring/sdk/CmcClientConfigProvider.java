package com.lichbalab.cmc.spring.sdk;

import com.lichbalab.cmc.sdk.CmcClientConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CmcClientConfigProvider {

    private final CmcClientProperties properties;

    public CmcClientConfigProvider(CmcClientProperties properties) {
        this.properties = properties;
    }

    @Bean
    public CmcClientConfig cmcClientConfig() {
        CmcClientConfig config = new CmcClientConfig();
        config.setBaseUrl(properties.getBaseUrl());
        return config;
    }
}