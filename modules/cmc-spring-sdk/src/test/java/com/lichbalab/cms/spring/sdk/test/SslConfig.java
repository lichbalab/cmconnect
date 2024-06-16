package com.lichbalab.cms.spring.sdk.test;

import com.lichbalab.certificate.Certificate;
import com.lichbalab.certificate.CertificateUtils;
import org.springframework.boot.ssl.DefaultSslBundleRegistry;
import org.springframework.boot.ssl.SslBundle;
import org.springframework.boot.ssl.SslStoreBundle;
import org.springframework.boot.ssl.pem.PemSslStoreBundle;
import org.springframework.boot.ssl.pem.PemSslStoreDetails;
import org.springframework.boot.web.embedded.tomcat.TomcatReactiveWebServerFactory;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.Ssl;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.FileReader;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;

//@Configuration
public class SslConfig {

/*
    @Bean
    public WebServerFactoryCustomizer<TomcatServletWebServerFactory> webServerFactoryCustomizer() throws Exception {
        //Path path = Paths.get(SslConfig.class.getResource("/certs").);
        //Certificate cert = CertificateUtils.buildFromPEM(new FileReader(path.toFile()));
        DefaultSslBundleRegistry bundles = new DefaultSslBundleRegistry("test",
                createPemSslBundle(SslConfig.class.getResource("/certs/cert.crt").toURI().toString(),
                        SslConfig.class.getResource("/certs/privatekey.key").toURI().toString()));


        return factory -> {

            factory.setSslBundles(bundles);
            factory.setSsl(Ssl.forBundle("test"));
            factory.setPort(8443);
        };
    }
*/

@Bean
public ServletWebServerFactory servletWebServerFactory() throws URISyntaxException {
    TomcatServletWebServerFactory factory = new TomcatServletWebServerFactory();
/*
    DefaultSslBundleRegistry bundles = new DefaultSslBundleRegistry("test",
            createPemSslBundle(SslConfig.class.getResource("/certs/cert.crt").toURI().toString(),
                    SslConfig.class.getResource("/certs/privatekey.key").toURI().toString()));

    factory.setSslBundles(bundles);
    factory.setSsl(Ssl.forBundle("test"));
    factory.setPort(8443);
*/
    return factory;
}


/*
    @Bean
    public TomcatReactiveWebServerFactory reactiveWebServerFactory() throws URISyntaxException {
        TomcatReactiveWebServerFactory factory = new TomcatReactiveWebServerFactory();
        DefaultSslBundleRegistry bundles = new DefaultSslBundleRegistry("test",
                createPemSslBundle(SslConfig.class.getResource("/certs/cert.crt").toURI().toString(),
                        SslConfig.class.getResource("/certs/privatekey.key").toURI().toString()));

        //factory.setSslBundles(bundles);
        //factory.setSsl(Ssl.forBundle("test"));
        //factory.setPort(8443);
        return factory;
    }
*/


    @Bean
    public TestController testController() {
        return new TestController();
    }

    protected SslBundle createPemSslBundle(String cert, String privateKey) {
        PemSslStoreDetails keyStoreDetails = PemSslStoreDetails.forCertificate(cert).withPrivateKey(privateKey);
        PemSslStoreDetails trustStoreDetails = PemSslStoreDetails.forCertificate(cert);
        SslStoreBundle stores = new PemSslStoreBundle(keyStoreDetails, trustStoreDetails);
        return SslBundle.of(stores);
    }

}
