package com.lichbalab.cmc.spring.sdk.test;

import com.lichbalab.cmc.spring.sdk.CmcSdkProperties;
import com.lichbalab.cmc.spring.sdk.CmcTomcatWebServerCustomizer;
import org.springframework.boot.ssl.SslBundles;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.Ssl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Primary;

@TestConfiguration
public class TestCmcSslBundleRegistryInitializerConfig {

    @Bean
    @DependsOn("cmcSslBundleRegistryInitializer")
    @Primary
    public CmcTomcatWebServerCustomizer cmcTomcatWebServerCustomizer(CmcSdkProperties properties) {
        return new CmcTomcatWebServerCustomizer(properties) {
            @Override
            public void customize(final TomcatServletWebServerFactory factory) {
                SslBundles sslBundles = CertConfig.createSslBundles(
                        CertConfig.PRIVATE_KEY_ALAIS_3,
                        CertConfig.CERT_ALAIS_3,
                        CertConfig.CERT_ALAIS_2
                );
                factory.setSslBundles(sslBundles);
                Ssl ssl = Ssl.forBundle(CertConfig.TEST_SSL_BUNDLE_NAME);
                ssl.setClientAuth(clientAuth);
                factory.setSsl(ssl);
            }
        };
    }
}