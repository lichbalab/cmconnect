package com.lichbalab.cmc.spring.sdk;

import org.springframework.boot.ssl.SslBundle;
import org.springframework.web.client.RestTemplate;

public interface CmcRestTemplate {

    RestTemplate getRestTemplate();

    void updateSslBundle();

}
