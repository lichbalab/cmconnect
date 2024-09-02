package com.lichbalab.cmc.spring.sdk;

import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.Ssl;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;

public class CmcTomcatWebServerCustomizer implements WebServerFactoryCustomizer<TomcatServletWebServerFactory> {
    public static Ssl.ClientAuth clientAuth;

    public CmcTomcatWebServerCustomizer(CmcSdkProperties properties) {
        if (StringUtils.isNotEmpty(properties.getClientAuth())) {
            clientAuth = Ssl.ClientAuth.valueOf(properties.getClientAuth());
        }
    }

    @Override
    public void customize(TomcatServletWebServerFactory factory) {

        factory.setSslBundles(CmcSslBundleRegistryProvider.getSslBundles());
        // Set the port to 0 to avoid port conflicts\
        // Webserver will choose a random port
        //factory.setPort(0);
        Ssl ssl = Ssl.forBundle(CmcDefaultSslBundleRegistry.SSL_BUNDLE_NAME);
        ssl.setClientAuth(clientAuth);
        factory.setSsl(ssl);
    }
}