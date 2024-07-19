package com.lichbalab.cmc.spring.sdk.test;

import com.lichbalab.certificate.Certificate;
import com.lichbalab.certificate.CertificateTestHelper;
import com.lichbalab.cmc.sdk.client.CmcClient;
import com.lichbalab.cmc.spring.sdk.CmcDefaultSslBundleRegistry;
import com.lichbalab.cmc.spring.sdk.CmcSslBundleRegistryInitializer;
import com.lichbalab.cmc.spring.sdk.CmcSslBundleRegistryProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ssl.SslBundleKey;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.Ssl;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TestTomcatWebServerCustomizer implements WebServerFactoryCustomizer<TomcatServletWebServerFactory> {

    public TomcatServletWebServerFactory factory;

    @Autowired
    private CmcClient cmcClient;

    private final static List<Certificate> CERTS = CertificateTestHelper.CERTS;

    @Autowired
    public CmcSslBundleRegistryInitializer sslBundleRegistryInitializer;

    @Override
    public void customize(TomcatServletWebServerFactory factory) {
        CERTS.stream()
                .filter(cert -> List.of(CertConfig.ALIAS_1, CertConfig.ALIAS_2).contains(cert.getAlias()))
                .forEach(cert -> cmcClient.addCertificate(cert));
        sslBundleRegistryInitializer.init(SslBundleKey.of(null, CertConfig.ALIAS_2));
        factory.setSslBundles(CmcSslBundleRegistryProvider.getSslBundles());
        factory.setSsl(Ssl.forBundle(CmcDefaultSslBundleRegistry.SSL_BUNDLE_NAME));
        this.factory = factory;
    }
}