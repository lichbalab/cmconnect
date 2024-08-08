package com.lichbalab.cmc.spring.sdk.test;

import com.lichbalab.certificate.Certificate;
import com.lichbalab.certificate.CertificateTestHelper;
import com.lichbalab.cmc.sdk.CmcClientConfig;
import com.lichbalab.cmc.sdk.client.CmcClient;
import com.lichbalab.cmc.sdk.client.CmcClientFactory;
import com.lichbalab.cmc.spring.sdk.CmcRestTemplateService;
import com.lichbalab.cmc.spring.sdk.CmcSdkProperties;
import com.lichbalab.cmc.spring.sdk.SslBundleRegistrySynchronizer;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.ssl.NoopHostnameVerifier;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactoryBuilder;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ssl.DefaultSslBundleRegistry;
import org.springframework.boot.ssl.SslBundle;
import org.springframework.boot.ssl.SslBundles;
import org.springframework.boot.ssl.SslStoreBundle;
import org.springframework.boot.ssl.pem.PemSslStoreBundle;
import org.springframework.boot.ssl.pem.PemSslStoreDetails;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.web.context.WebServerApplicationContext;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.Ssl;
import org.springframework.boot.web.server.WebServer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.web.client.RestTemplate;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.junit.jupiter.Testcontainers;

import javax.net.ssl.SSLHandshakeException;
import java.net.URISyntaxException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = TestApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(classes = {TestCmcSslBundleRegistryInitializerConfig.class})
public class CmcSpringSdkTomcatClientIT extends BaseIntegrationTest {

    @Autowired
    private CmcClient cmcClient;

    @Autowired
    private SslBundleRegistrySynchronizer sslBundleRegistrySynchronizer;

    @Autowired
    private CmcRestTemplateService cmcRestTemplate;

    @DynamicPropertySource
    public static void setCmcClientProperties(DynamicPropertyRegistry registry) {
        registry.add("TEST_CMC_API_PORT", () -> CMC_API.getMappedPort(API_EXPOSED_PORT));
        registry.add("TEST_CMC_CLIENT_AUTH", Ssl.ClientAuth.NONE::name);
        registry.add("TEST_CMC_CRON", () -> "0 0 0 * * * ");
        registry.add("TEST_KEY_ALIAS", () -> CertConfig.ALIAS_2);
        registry.add("TEST_DISABLE_HOSTNAME_VERIFICATION", () -> "true");
    }

    @Override
    protected int getPort() {
        return port;
    }

    @LocalServerPort
    private int port;

    @Test
    void testRestTemplateWitManageableSslBundle() {
        // clean up all certificates
        CERTS.forEach(cert -> cmcClient.deleteCertificate(cert.getAlias()));
        CERTS.stream()
                .filter(cert -> List.of(CertConfig.ALIAS_1, CertConfig.ALIAS_2).contains((cert.getAlias())))
                .forEach(cert -> cmcClient.addCertificate(cert));

        callApiAndCheckHandshakeException(cmcRestTemplate.getRestTemplate());

        CERTS.stream()
                .filter(cert -> CertConfig.CERT_ALAIS_3.equals(cert.getAlias()))
                .forEach(cert -> cmcClient.addCertificate(cert));

        // Update SSL context for the REST API endpoint with server certificate with alias "lichbalab3.pem"
        sslBundleRegistrySynchronizer.synchronize(CertConfig.ALIAS_2);

        callApiAndCheckResponse(cmcRestTemplate.getRestTemplate());
    }
}