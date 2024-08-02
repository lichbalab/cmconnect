package com.lichbalab.cmc.spring.sdk.test;

import com.lichbalab.certificate.Certificate;
import com.lichbalab.certificate.CertificateTestHelper;
import com.lichbalab.cmc.sdk.client.CmcClient;
import com.lichbalab.cmc.spring.sdk.SslBundleRegistrySynchronizer;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.ssl.NoopHostnameVerifier;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactoryBuilder;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ssl.DefaultSslBundleRegistry;
import org.springframework.boot.ssl.SslBundle;
import org.springframework.boot.ssl.SslBundles;
import org.springframework.boot.ssl.SslStoreBundle;
import org.springframework.boot.ssl.pem.PemSslStoreBundle;
import org.springframework.boot.ssl.pem.PemSslStoreDetails;
import org.springframework.boot.web.context.WebServerApplicationContext;
import org.springframework.boot.web.server.Ssl;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
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
public class CmcSpringSdkTomcatServerIT {

    static final Logger logger = LoggerFactory.getLogger(CmcSpringSdkTomcatServerIT.class);

    private static final String TEST_SSL_BUNDLE_NAME = "test";
    private final static List<Certificate> CERTS = CertificateTestHelper.CERTS;
    private final static int API_EXPOSED_PORT = 8080;

    private static PostgreSQLContainer<?> POSTGRES_CONTAINER;
    private static GenericContainer<?> CMC_API;

    private CmcClient cmcClient;
    private SslBundleRegistrySynchronizer sslBundleRegistrySynchronizer;

    @BeforeAll
    public static void beforeAll() {
        Network network = Network.newNetwork();

        POSTGRES_CONTAINER = new PostgreSQLContainer<>("postgres:latest")
                .withDatabaseName("testdb")
                .withUsername("test")
                .withPassword("test")
                .withNetwork(network).
                withNetworkAliases("postgres");

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
    }

    @AfterAll
    public static void afterAll() {
        CMC_API.stop();
        POSTGRES_CONTAINER.stop();
    }

    public void before() {
        SpringContextUtil.startContext("--cmc.client.baseUrl=http://localhost:" + CMC_API.getMappedPort(API_EXPOSED_PORT));
        cmcClient = SpringContextUtil.getContext().getBean(CmcClient.class);
        sslBundleRegistrySynchronizer = SpringContextUtil.getContext().getBean(SslBundleRegistrySynchronizer.class);
    }

    @AfterEach
    public void after() {
        SpringContextUtil.stopContext();
    }

    @Test
    public void test1WaySslListenerWitRestTemplate() {
        TestTomcatWebServerCustomizer.clientAuth = null;
        before();
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
        callApiAndCechHandshakeException(restTemplate);

        // Load required certificate to escape SSLHandshakeException
        CERTS.stream()
                .filter(cert -> CertConfig.ALIAS_3.equals(cert.getAlias()))
                .forEach(cert -> cmcClient.addCertificate(cert));

        // Update SSL context for the REST API endpoint with server certificate with alias "lichbalab3.pem"
        sslBundleRegistrySynchronizer.synchronize(CertConfig.ALIAS_3);

        ResponseEntity<String> response = callRestApi(restTemplate);
        Assertions.assertNotNull(response, "Response is null");
        assertThat(response.getBody()).isEqualTo("Hello, World!");

        // Update SSL context for the REST API endpoint with server certificate with alias "lichbalab2.pem"
        sslBundleRegistrySynchronizer.synchronize(CertConfig.ALIAS_2);
        // Should result in SSLHandshakeException because the server certificate is not trusted by the client
        // which can trust only the certificate with alias "lichbalab3.pem"
        callApiAndCechHandshakeException(restTemplate);
    }

    @Test
    void test2WaySslListenerWitRestTemplate() {
        TestTomcatWebServerCustomizer.clientAuth = Ssl.ClientAuth.NEED;
        before();
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

        callApiAndCechHandshakeException(restTemplate);

        CERTS.stream()
                .filter(cert -> CertConfig.CERT_ALAIS_3.equals(cert.getAlias()))
                .forEach(cert -> cmcClient.addCertificate(cert));

        // Update SSL context for the REST API endpoint with server certificate with alias "lichbalab3.pem"
        sslBundleRegistrySynchronizer.synchronize(CertConfig.ALIAS_2);

        callApiAndCheckResponse(restTemplate);
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

    void callApiAndCheckResponse(RestTemplate restTemplate) {
        ResponseEntity<String> response = callRestApi(restTemplate);
        Assertions.assertNotNull(response, "Response is null");
        assertThat(response.getBody()).isEqualTo("Hello, World!");
    }


    private ResponseEntity<String> callRestApi(RestTemplate restTemplate) {
        HttpHeaders headers = new HttpHeaders();
        // close connection after the request is completed, to avoid connection reuse
        // and initialization of SSL handshake for each request.
        headers.setConnection("close");

        HttpEntity<String> entity = new HttpEntity<>(headers);

        // Use the exchange method to send the request and receive a response
        return restTemplate.exchange(
                "https://127.0.0.1:" +
                        ((WebServerApplicationContext) SpringContextUtil.getContext()).getWebServer().getPort() +
                        "/test/hello",
                HttpMethod.GET,
                entity,
                String.class
        );
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

    protected SslBundles createSslBundles(String privateKeyAlias, String certAlias, String trustStoreAlias) {
        DefaultSslBundleRegistry bundles;
        String sslBundlesPath = "/ssl-bundles/";
        try {
            bundles = new DefaultSslBundleRegistry(TEST_SSL_BUNDLE_NAME,
                    createPemSslBundle(CmcSpringSdkTomcatServerIT.class.getResource(sslBundlesPath + certAlias).toURI().toString(),
                            CmcSpringSdkTomcatServerIT.class.getResource(sslBundlesPath + privateKeyAlias).toURI().toString(),
                            CmcSpringSdkTomcatServerIT.class.getResource(sslBundlesPath + trustStoreAlias).toURI().toString()
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
}