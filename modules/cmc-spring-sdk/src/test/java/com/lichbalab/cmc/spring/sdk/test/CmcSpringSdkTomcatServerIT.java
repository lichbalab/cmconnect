package com.lichbalab.cmc.spring.sdk.test;

import com.lichbalab.cmc.sdk.CmcClientConfig;
import com.lichbalab.cmc.sdk.client.CmcClient;
import com.lichbalab.cmc.sdk.client.CmcClientFactory;
import com.lichbalab.cmc.spring.sdk.CmcSslBundleRegistryProvider;
import com.lichbalab.cmc.spring.sdk.SslBundleRegistrySynchronizer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.ssl.SslBundle;
import org.springframework.boot.web.context.WebServerApplicationContext;
import org.springframework.boot.web.server.Ssl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class CmcSpringSdkTomcatServerIT extends BaseIntegrationTest {

    private CmcClient cmcClient;
    private SslBundleRegistrySynchronizer sslBundleRegistrySynchronizer;

/*
    @BeforeAll
    public static void beforeAll() {
        BaseIntegrationTest.beforeAll();
        CmcClientConfig config = new CmcClientConfig();
        config.setBaseUrl("http://localhost:" + CMC_API.getMappedPort(API_EXPOSED_PORT));
        CmcClient cmcClient = CmcClientFactory.createService(config);
        CERTS.stream()
                .filter(cert -> List.of(CertConfig.ALIAS_1, CertConfig.ALIAS_2).contains(cert.getAlias()))
                .forEach(cmcClient::addCertificate);

    }
*/


    public void before(String clientAuth, String keyAlias) {
        before(
                "--TEST_CMC_API_PORT=" + CMC_API.getMappedPort(API_EXPOSED_PORT),
                "--TEST_CMC_CLIENT_AUTH=" + clientAuth,
                "--TEST_KEY_ALIAS=" + keyAlias
        );
    }

    @AfterEach
    public void after() {
        SpringContextUtil.stopContext();
        CmcSslBundleRegistryProvider.getRegistry().clear();
    }

    @Test
    public void test1WaySslListenerWitRestTemplate() {
        before(Ssl.ClientAuth.NONE.name(), CertConfig.ALIAS_2);
        SslBundle sslBundle = createSslBundles(
                CertConfig.PRIVATE_KEY_ALAIS_3,
                CertConfig.CERT_ALAIS_3,
                CertConfig.CERT_ALAIS_3
        ).getBundle(TEST_SSL_BUNDLE_NAME);

        RestTemplate restTemplate = createRestTemplate(sslBundle);

        // Make a GET request to the /hello endpoint
        // The server certificate lichbalab2.pem is not trusted by the client
        // which can trust only the certificate with alias "lichbalab3.pem",
        // so it should result in SSLHandshakeException
        callApiAndCheckHandshakeException(restTemplate);

        // Load required certificate to escape SSLHandshakeException
        CERTS.stream()
                .filter(cert -> CertConfig.ALIAS_3.equals(cert.getAlias()))
                .forEach(cert -> cmcClient.addCertificate(cert));

        // Update SSL context for the REST API endpoint with server certificate with alias "lichbalab3.pem"
        sslBundleRegistrySynchronizer.synchronize(CertConfig.ALIAS_3);

        callApiAndCheckResponse(restTemplate);

        // Update SSL context for the REST API endpoint with server certificate with alias "lichbalab2.pem"
        sslBundleRegistrySynchronizer.synchronize(CertConfig.ALIAS_2);
        // Should result in SSLHandshakeException because the server certificate is not trusted by the client
        // which can trust only the certificate with alias "lichbalab3.pem"
        callApiAndCheckHandshakeException(restTemplate);
    }

    @Test
    void test2WaySslListenerWitRestTemplate() {
        before(Ssl.ClientAuth.NEED.name(), CertConfig.ALIAS_2);
        // clean up all certificates
        CERTS.forEach(cert -> cmcClient.deleteCertificate(cert.getAlias()));
        SslBundle sslBundle = createSslBundles(
                CertConfig.PRIVATE_KEY_ALAIS_3,
                CertConfig.CERT_ALAIS_3,
                CertConfig.CERT_ALAIS_2
        ).getBundle(TEST_SSL_BUNDLE_NAME);
        RestTemplate restTemplate = createRestTemplate(sslBundle);

        CERTS.stream()
                .filter(cert -> List.of(CertConfig.ALIAS_1, CertConfig.ALIAS_2).contains((cert.getAlias())))
                .forEach(cert -> cmcClient.addCertificate(cert));

        callApiAndCheckHandshakeException(restTemplate);

        CERTS.stream()
                .filter(cert -> CertConfig.CERT_ALAIS_3.equals(cert.getAlias()))
                .forEach(cert -> cmcClient.addCertificate(cert));

        // Update SSL context for the REST API endpoint with server certificate with alias "lichbalab3.pem"
        sslBundleRegistrySynchronizer.synchronize(CertConfig.ALIAS_2);

        callApiAndCheckResponse(restTemplate);
    }

    @Override
    protected int getPort() {
        return ((WebServerApplicationContext) SpringContextUtil.getContext()).getWebServer().getPort();
    }


    /*private RestTemplate createRestTemplate(SslBundle sslBundle) {
        // Create an HttpClient that uses the custom SSLContext
        CloseableHttpClient httpClient = HttpClients.custom()
                .setConnectionManager(PoolingHttpClientConnectionManagerBuilder.create()
                        .setSSLSocketFactory(SSLConnectionSocketFactoryBuilder.create()
                                .setSslContext(sslBundle.createSslContext())
                                .setHostnameVerifier(NoopHostnameVerifier.INSTANCE)
                                .build())
                        .build())
                .build();        // Create a RestTemplate that uses the custom HttpClient
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
        requestFactory.setHttpClient(httpClient);

        return new RestTemplate(requestFactory);
    }
*/

}