package com.lichbalab.cmc.spring.sdk.test;

import org.springframework.boot.ssl.DefaultSslBundleRegistry;
import org.springframework.boot.ssl.SslBundle;
import org.springframework.boot.ssl.SslStoreBundle;
import org.springframework.boot.ssl.pem.PemSslStoreBundle;
import org.springframework.boot.ssl.pem.PemSslStoreDetails;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.Ssl;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.stereotype.Component;

import java.net.URISyntaxException;

@Component
public class MyTomcatWebServerCustomizer implements WebServerFactoryCustomizer<TomcatServletWebServerFactory> {

    @Override
    public void customize(TomcatServletWebServerFactory factory) {
        DefaultSslBundleRegistry bundles = null;
        try {
            bundles = new DefaultSslBundleRegistry("test",
                    createPemSslBundle(SslConfig.class.getResource("/certs/cert.crt").toURI().toString(),
                            SslConfig.class.getResource("/certs/privatekey.key").toURI().toString()));
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        factory.setSslBundles(bundles);
        factory.setSsl(Ssl.forBundle("test"));
        // customize the factory here
    }

    protected SslBundle createPemSslBundle(String cert, String privateKey) {
        PemSslStoreDetails keyStoreDetails = PemSslStoreDetails.forCertificate(cert).withPrivateKey(privateKey);
        PemSslStoreDetails trustStoreDetails = PemSslStoreDetails.forCertificate(cert);
        SslStoreBundle stores = new PemSslStoreBundle(keyStoreDetails, trustStoreDetails);
        return SslBundle.of(stores);
    }
}