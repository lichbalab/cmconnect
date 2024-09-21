package com.lichbalab.cmc.spring.sdk.test;

import com.lichbalab.certificate.Certificate;
import com.lichbalab.certificate.CertificateTestHelper;
import com.lichbalab.cmc.sdk.CmcClientConfig;
import com.lichbalab.cmc.sdk.client.CmcClient;
import com.lichbalab.cmc.sdk.client.CmcClientFactory;
import com.lichbalab.cmc.spring.sdk.CmcSslBundleRegistryProvider;
import com.lichbalab.cmc.spring.sdk.SslBundleRegistrySynchronizer;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.ssl.NoopHostnameVerifier;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactoryBuilder;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ssl.DefaultSslBundleRegistry;
import org.springframework.boot.ssl.SslBundle;
import org.springframework.boot.ssl.SslBundles;
import org.springframework.boot.ssl.SslStoreBundle;
import org.springframework.boot.ssl.pem.PemSslStoreBundle;
import org.springframework.boot.ssl.pem.PemSslStoreDetails;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.web.client.RestTemplate;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;

import javax.net.ssl.SSLHandshakeException;
import java.net.URISyntaxException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public abstract class BaseIntegrationTest {

    protected static final Logger logger = LoggerFactory.getLogger(BaseIntegrationTest.class);

    protected static final String TEST_SSL_BUNDLE_NAME = "test";
    protected static final int API_EXPOSED_PORT = 8080;

    protected static PostgreSQLContainer<?> POSTGRES_CONTAINER;
    protected static GenericContainer<?> CMC_API;

    @Autowired
    protected CmcClient cmcClient;

    @Autowired
    protected SslBundleRegistrySynchronizer sslBundleRegistrySynchronizer;

    protected static final List<Certificate> CERTS = CertificateTestHelper.CERTS;

    @BeforeAll
    public static void beforeAll() {
        Network network = Network.newNetwork();

        POSTGRES_CONTAINER = new PostgreSQLContainer<>("postgres:latest")
                .withDatabaseName("testdb")
                .withUsername("test")
                .withPassword("test")
                .withNetwork(network)
                .withNetworkAliases("postgres");

        POSTGRES_CONTAINER.start();

        CMC_API = new GenericContainer<>("lichbalab:cmc-2024.1")
                .withExposedPorts(API_EXPOSED_PORT)
                .withNetwork(network)
                .withLogConsumer(new Slf4jLogConsumer(logger))
                .dependsOn(POSTGRES_CONTAINER)
                .withEnv("DB_HOST", "postgres")
                .withEnv("DB_PORT", String.valueOf(POSTGRES_CONTAINER.getExposedPorts().getFirst()))
                .withEnv("DB_NAME", POSTGRES_CONTAINER.getDatabaseName())
                .withEnv("DB_USERNAME", POSTGRES_CONTAINER.getUsername())
                .withEnv("DB_PASSWORD", POSTGRES_CONTAINER.getPassword());
        CMC_API.start();

        CmcClientConfig config = new CmcClientConfig();
        config.setBaseUrl("http://localhost:" + CMC_API.getMappedPort(API_EXPOSED_PORT));
        CmcClient cmcClient = CmcClientFactory.createService(config);
        CERTS.stream()
                .filter(cert -> List.of(CertConfig.ALIAS_1, CertConfig.ALIAS_2).contains(cert.getAlias()))
                .forEach(cmcClient::addCertificate);
    }

    @AfterAll
    public static void afterAll() {
        CMC_API.stop();
        POSTGRES_CONTAINER.stop();
        SpringContextUtil.stopContext();
        CmcSslBundleRegistryProvider.getRegistry().clear();
    }

    @AfterEach
    public void after() {
        // Clear the SSL bundle registry after each test
        //CmcSslBundleRegistryProvider.getRegistry().clear();
    }

    @DynamicPropertySource
    public static void setCmcClientProperties(DynamicPropertyRegistry registry) {
        registry.add("cmc.client.baseUrl", () -> "http://localhost:" + CMC_API.getMappedPort(API_EXPOSED_PORT));
    }

    protected void before(String ... args) {
        SpringContextUtil.startContext(args);
        cmcClient = SpringContextUtil.getContext().getBean(CmcClient.class);
        sslBundleRegistrySynchronizer = SpringContextUtil.getContext().getBean(SslBundleRegistrySynchronizer.class);
    }


    protected RestTemplate createRestTemplate(SslBundle sslBundle) {
        CloseableHttpClient httpClient = HttpClients.custom()
                .setConnectionManager(PoolingHttpClientConnectionManagerBuilder.create()
                        .setSSLSocketFactory(SSLConnectionSocketFactoryBuilder.create()
                                .setSslContext(sslBundle.createSslContext())
                                .setHostnameVerifier(NoopHostnameVerifier.INSTANCE)
                                .build())
                        .build())
                .build();

        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
        requestFactory.setHttpClient(httpClient);

        return new RestTemplate(requestFactory);
    }

    protected void callApiAndCheckHandshakeException(RestTemplate restTemplate) {
        Exception ex = null;
        try {
            callRestApi(restTemplate);
        } catch (Exception e) {
            ex = e;
            assertHandshakeException(e);
        }
        assertThat(ex).isNotNull();
    }

    protected void callApiAndCheckResponse(RestTemplate restTemplate) {
        ResponseEntity<String> response = callRestApi(restTemplate);
        assertThat(response).isNotNull();
        assertThat(response.getBody()).isEqualTo("Hello, World!");
    }

    protected ResponseEntity<String> callRestApi(RestTemplate restTemplate) {
        HttpHeaders headers = new HttpHeaders();
        headers.setConnection("close");

        HttpEntity<String> entity = new HttpEntity<>(headers);

        return restTemplate.exchange(
                "https://127.0.0.1:" + getPort() + "/test/hello",
                HttpMethod.GET,
                entity,
                String.class
        );
    }

    protected void assertHandshakeException(Throwable cause) {
        boolean handshakeExceptionFound = false;
        while (cause != null) {
            if (cause instanceof SSLHandshakeException) {
                handshakeExceptionFound = true;
                break;
            }
            cause = cause.getCause();
        }
        if (!handshakeExceptionFound) {
            throw new AssertionError("SSLHandshakeException was not found in the cause chain");
        }
    }

    protected SslBundles createSslBundles(String privateKeyAlias, String certAlias, String trustStoreAlias) {
        DefaultSslBundleRegistry bundles;
        String sslBundlesPath = "/ssl-bundles/";
        try {
            bundles = new DefaultSslBundleRegistry(TEST_SSL_BUNDLE_NAME,
                    createPemSslBundle(BaseIntegrationTest.class.getResource(sslBundlesPath + certAlias).toURI().toString(),
                            BaseIntegrationTest.class.getResource(sslBundlesPath + privateKeyAlias).toURI().toString(),
                            BaseIntegrationTest.class.getResource(sslBundlesPath + trustStoreAlias).toURI().toString()
                    ));
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        return bundles;
    }

    protected SslBundle createPemSslBundle(String cert, String privateKey, String trustCert) {
        PemSslStoreDetails keyStoreDetails = PemSslStoreDetails.forCertificate(cert).withPrivateKey(privateKey);
        PemSslStoreDetails trustStoreDetails = PemSslStoreDetails.forCertificate(trustCert);
        SslStoreBundle stores = new PemSslStoreBundle(keyStoreDetails, trustStoreDetails);
        return SslBundle.of(stores);
    }

    protected abstract int getPort();
}