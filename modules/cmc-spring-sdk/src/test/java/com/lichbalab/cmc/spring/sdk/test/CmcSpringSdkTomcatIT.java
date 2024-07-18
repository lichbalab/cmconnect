package com.lichbalab.cmc.spring.sdk.test;

import com.lichbalab.certificate.Certificate;
import com.lichbalab.certificate.CertificateTestHelper;
import com.lichbalab.cmc.sdk.CmcClientConfig;
import com.lichbalab.cmc.sdk.client.CmcClient;
import com.lichbalab.cmc.spring.sdk.SslBundleRegistrySynchronizer;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.ssl.NoopHostnameVerifier;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactoryBuilder;
import org.junit.Assert;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ssl.DefaultSslBundleRegistry;
import org.springframework.boot.ssl.SslBundle;
import org.springframework.boot.ssl.SslBundleKey;
import org.springframework.boot.ssl.SslBundles;
import org.springframework.boot.ssl.SslStoreBundle;
import org.springframework.boot.ssl.pem.PemSslStoreBundle;
import org.springframework.boot.ssl.pem.PemSslStoreDetails;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
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

@Testcontainers
@SpringBootTest(classes = TestApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CmcSpringSdkTomcatIT {

    static final Logger logger = LoggerFactory.getLogger(CmcSpringSdkTomcatIT.class);

    private static final String TEST_SSL_BUNDLE_NAME = "test";

    private static PostgreSQLContainer<?> POSTGRES_CONTAINER;
    private static GenericContainer<?> CMC_API;

    @Autowired
    private CmcClient cmcClient;

    @Autowired
    private SslBundleRegistrySynchronizer sslBundleRegistrySynchronizer;

    private final static List<Certificate> CERTS = CertificateTestHelper.CERTS;


    @BeforeAll
    public static void beforeAll() {
        Network network = Network.newNetwork();
        int API_EXPOSED_POPRT = 8080;

        POSTGRES_CONTAINER = new PostgreSQLContainer<>("postgres:latest")
                .withDatabaseName("testdb")
                .withUsername("test")
                .withPassword("test")
                .withNetwork(network).
                withNetworkAliases("postgres");

        POSTGRES_CONTAINER.start();

        CMC_API = new GenericContainer<>("lichbalab:cmc-2024.1")
                .withExposedPorts(API_EXPOSED_POPRT)
                .withNetwork(network)
                .withLogConsumer(new Slf4jLogConsumer(logger))
                .dependsOn(POSTGRES_CONTAINER)
                .withEnv("DB_HOST", "postgres")
                .withEnv("DB_PORT", String.valueOf(POSTGRES_CONTAINER.getExposedPorts().getFirst()))
                .withEnv("DB_NAME", POSTGRES_CONTAINER.getDatabaseName())
                .withEnv("DB_USERNAME", POSTGRES_CONTAINER.getUsername())
                .withEnv("DB_PASSWORD", POSTGRES_CONTAINER.getPassword());
        CMC_API.start();
    }

    @AfterAll
    public static void afterAll() throws Exception {
        CMC_API.stop();
        POSTGRES_CONTAINER.stop();
    }

    @LocalServerPort
    private int port;

    @Test
    public void test1WaySslListenerWitRestTemplate() throws Exception {
        //CERTS.forEach(cert -> cmcClient.addCertificate(cert));

        SslBundle sslBundle = createSslBundles().getBundle(TEST_SSL_BUNDLE_NAME);
        RestTemplate restTemplate = createRestTemplate(sslBundle);

        // Make a GET request to the /hello endpoint
        callApiAndCechHandshakeException(restTemplate);

        // load required certificate to escape SSLHandshakeException
        CERTS.stream()
                .filter(cert -> TestTomcatWebServerCustomizer.ALIASES.getLast().equals(cert.getAlias()))
                .forEach(cert -> cmcClient.addCertificate(cert));
        sslBundleRegistrySynchronizer.synchronize(SslBundleKey.of(null, TestTomcatWebServerCustomizer.ALIASES.getLast()));

        ResponseEntity<String> response = callRestApi(restTemplate);
        Assertions.assertNotNull(response, "Response is null");
        assertThat(response.getBody()).isEqualTo("Hello, World!");

        //sslBundleRegistrySynchronizer.synchronize(SslBundleKey.of(null, TestTomcatWebServerCustomizer.ALIASES.get(1)));
        //callApiAndCechHandshakeException(restTemplate);
    }

    @Test
    void test2WaySslListenerWitRestTemplate() {
        //CERTS.forEach(cert -> cmcClient.addCertificate(cert));

        SslBundle sslBundle = createSslBundles().getBundle(TEST_SSL_BUNDLE_NAME);
    }

    private RestTemplate createRestTemplate(SslBundle sslBundle) {
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

    void callApiAndCechHandshakeException(RestTemplate restTemplate) {
        Exception ex = null;
        try {
            callRestApi(restTemplate);
        } catch (Exception e) {
            ex = e;
            assertHandshakeException(e);
        }
        Assertions.assertNotNull(ex, "SSLHandshakeException was not thrown");
    }


    private ResponseEntity<String> callRestApi(RestTemplate restTemplate) {
        return restTemplate.getForEntity("https://127.0.0.1:" + port + "/test/hello", String.class);
    }

    private void assertHandshakeException(Throwable cause) {
        boolean handshakeExceptionFound = false;
        while (cause != null) {
            if (cause instanceof SSLHandshakeException) {
                handshakeExceptionFound = true;
                break; // SSLHandshakeException found, exit loop
            }
            cause = cause.getCause(); // Move to the next cause
        }
        if (!handshakeExceptionFound) {
            Assertions.fail("SSLHandshakeException was not found in the cause chain");
        }
    }


    public void testHttpsEndpointWithWebCLient() {
        /*
        WebClient.Builder webClientBuilder = WebClient.builder();

        SslContextBuilder sslCont = SslContextBuilder.forClient()
                .trustManager(InsecureTrustManagerFactory.INSTANCE);


        HttpClient httpClient = HttpClient.create().secure(sslSpec -> sslSpec.sslContext(sslCont));

        WebClient webClient = webClientBuilder
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .baseUrl("https://localhost:" + port)
                .build();

        Mono<String> response = webClient.get()
                .uri("/hello")
                .retrieve()
                .bodyToMono(String.class);

        String result = response.block();
        assertThat(result).isEqualTo("Hello, World!");
*/

    }

    protected SslBundles createSslBundles() {
        DefaultSslBundleRegistry bundles;
        try {
            bundles = new DefaultSslBundleRegistry(TEST_SSL_BUNDLE_NAME,
                    createPemSslBundle(CmcSpringSdkTomcatIT.class.getResource("/ssl-bundles/lichbalab3.crt").toURI().toString(),
                            CmcSpringSdkTomcatIT.class.getResource("/ssl-bundles/lichbalab3.key").toURI().toString()));
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        return bundles;
    }

    protected SslBundle createPemSslBundle(String cert, String privateKey) {
        PemSslStoreDetails keyStoreDetails = PemSslStoreDetails.forCertificate(cert).withPrivateKey(privateKey);
        PemSslStoreDetails trustStoreDetails = PemSslStoreDetails.forCertificate(cert);
        SslStoreBundle stores = new PemSslStoreBundle(keyStoreDetails, trustStoreDetails);
        return SslBundle.of(stores);
    }
}