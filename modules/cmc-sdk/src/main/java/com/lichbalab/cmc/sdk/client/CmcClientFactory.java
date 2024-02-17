package com.lichbalab.cmc.sdk.client;

import com.lichbalab.cmc.sdk.CmcClientConfig;
import org.springframework.web.reactive.function.client.WebClient;

public class CmcClientFactory {

    public static CmcClient createService(CmcClientConfig config) {
        // Manually create and configure WebClient or other Spring components here
        WebClient webClient = WebClient.builder()
                 .baseUrl(config.getBaseUrl())
                 // Configure SSL, etc. based on config
                 .build();

        return new CmcClientImpl(webClient);
    }
}