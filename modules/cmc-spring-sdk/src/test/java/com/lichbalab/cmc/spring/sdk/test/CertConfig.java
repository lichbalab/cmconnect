package com.lichbalab.cmc.spring.sdk.test;

import org.springframework.boot.ssl.DefaultSslBundleRegistry;
import org.springframework.boot.ssl.SslBundle;
import org.springframework.boot.ssl.SslBundles;
import org.springframework.boot.ssl.SslStoreBundle;
import org.springframework.boot.ssl.pem.PemSslStoreBundle;
import org.springframework.boot.ssl.pem.PemSslStoreDetails;

import java.net.URISyntaxException;

public class CertConfig {

    protected static final String TEST_SSL_BUNDLE_NAME = "test";

    public static final String ALIAS_1 = "lichbalab1.pem";
    public static final String ALIAS_2 = "lichbalab2.pem";
    public static final String ALIAS_3 = "lichbalab3.pem";

    public static final String PRIVATE_KEY_ALAIS_3 = "lichbalab3.key";
    public static final String CERT_ALAIS_3 = "lichbalab3.crt";
    public static final String CERT_ALAIS_2 = "lichbalab2.crt";


    public static SslBundles createSslBundles(String privateKeyAlias, String certAlias, String trustStoreAlias) {
        DefaultSslBundleRegistry bundles;
        String sslBundlesPath = "/ssl-bundles/";
        try {
            bundles = new DefaultSslBundleRegistry(TEST_SSL_BUNDLE_NAME,
                    createPemSslBundle(CmcSpringSdkTomcatClientIT.class.getResource(sslBundlesPath + certAlias).toURI().toString(),
                            CmcSpringSdkTomcatClientIT.class.getResource(sslBundlesPath + privateKeyAlias).toURI().toString(),
                            CmcSpringSdkTomcatClientIT.class.getResource(sslBundlesPath + trustStoreAlias).toURI().toString()
                    ));
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        return bundles;
    }

    public static SslBundle createPemSslBundle(String cert, String privateKey, String trustCert) {
        PemSslStoreDetails keyStoreDetails = PemSslStoreDetails.forCertificate(cert).withPrivateKey(privateKey);
        PemSslStoreDetails trustStoreDetails = PemSslStoreDetails.forCertificate(trustCert);
        SslStoreBundle stores = new PemSslStoreBundle(keyStoreDetails, trustStoreDetails);
        return SslBundle.of(stores);
    }



}
