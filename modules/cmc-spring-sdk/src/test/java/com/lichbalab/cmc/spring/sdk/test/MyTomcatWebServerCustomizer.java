package com.lichbalab.cmc.spring.sdk.test;

import com.lichbalab.certificate.Certificate;
import com.lichbalab.certificate.CertificateTestHelper;
import com.lichbalab.cmc.sdk.client.CmcClient;
import com.lichbalab.cmc.spring.sdk.CmcDefaultSslBundleRegistry;
import com.lichbalab.cmc.spring.sdk.CmcSslBundleRegistryInitializer;
import com.lichbalab.cmc.spring.sdk.CmcSslBundleRegistryProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ssl.DefaultSslBundleRegistry;
import org.springframework.boot.ssl.SslBundle;
import org.springframework.boot.ssl.SslBundleKey;
import org.springframework.boot.ssl.SslStoreBundle;
import org.springframework.boot.ssl.pem.PemSslStoreBundle;
import org.springframework.boot.ssl.pem.PemSslStoreDetails;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.Ssl;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.stereotype.Component;

import java.net.URISyntaxException;
import java.util.List;

@Component
public class MyTomcatWebServerCustomizer implements WebServerFactoryCustomizer<TomcatServletWebServerFactory> {

    public static final List<String> ALIASES = List.of("lichbalab1.pem", "lichbalab2.pem", "lichbalab3.pem");

    @Autowired
    private CmcClient cmcClient;

    private final static List<Certificate> CERTS = CertificateTestHelper.CERTS;

    @Autowired
    public CmcSslBundleRegistryInitializer sslBundleRegistryInitializer;

    @Override
    public void customize(TomcatServletWebServerFactory factory) {
/*
        DefaultSslBundleRegistry bundles = null;
        try {
            bundles = new DefaultSslBundleRegistry("test",
                    createPemSslBundle(SslConfig.class.getResource("/ssl-bundles/cert.crt").toURI().toString(),
                            SslConfig.class.getResource("/ssl-bundles/privatekey.key").toURI().toString()));
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
*/

        CERTS.stream()
                .filter(cert -> !ALIASES.getLast().equals(cert.getAlias()))
                .forEach(cert -> cmcClient.addCertificate(cert));
        sslBundleRegistryInitializer.init(SslBundleKey.of(null, ALIASES.get(1)));
        factory.setSslBundles(CmcSslBundleRegistryProvider.getSslBundles());
        factory.setSsl(Ssl.forBundle(CmcDefaultSslBundleRegistry.SSL_BUNDLE_NAME));
        // customize the factory here
    }

    protected SslBundle createPemSslBundle(String cert, String privateKey) {
        PemSslStoreDetails keyStoreDetails = PemSslStoreDetails.forCertificate(cert).withPrivateKey(privateKey);
        PemSslStoreDetails trustStoreDetails = PemSslStoreDetails.forCertificate(cert);
        SslStoreBundle stores = new PemSslStoreBundle(keyStoreDetails, trustStoreDetails);
        return SslBundle.of(stores);
    }
}