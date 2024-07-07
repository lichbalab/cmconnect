package com.lichbalab.cmc.spring.sdk.test;

import com.lichbalab.cmc.sdk.CmcClientConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.ssl.NoopHostnameVerifier;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactoryBuilder;
import org.apache.hc.client5.http.ssl.TrustAllStrategy;
import org.apache.hc.core5.ssl.SSLContextBuilder;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@SpringBootTest(classes = TestApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@ComponentScan(basePackages = "com.lichbalab.cmc.spring.sdk.test") // Scan the test package
//@ContextConfiguration(classes = {SslConfig.class, MyTomcatWebServerCustomizer.class}) // Import the SSL config and the test controller
public class CmcSpringSdkIT {

    static final Logger logger = LoggerFactory.getLogger(CmcSpringSdkIT.class);

    private static PostgreSQLContainer<?> POSTGRES_CONTAINER;
    private static GenericContainer<?> CMC_API;
    private static CmcClientConfig CMS_CONFIG;

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


    @BeforeEach
    public void setUp() {
    }


/*
    @Autowired
    private WebClient.Builder webClientBuilder;
*/

    @Test
    public void testHttpsEndpointWitRestTemplate() throws Exception {
        // Create an HttpClient that uses the custom SSLContext
        CloseableHttpClient httpClient = HttpClients.custom()
                .setConnectionManager(PoolingHttpClientConnectionManagerBuilder.create()
                        .setSSLSocketFactory(SSLConnectionSocketFactoryBuilder.create()
                                .setSslContext(SSLContextBuilder.create()
                                        .loadTrustMaterial(TrustAllStrategy.INSTANCE)
                                        .build())
                                .setHostnameVerifier(NoopHostnameVerifier.INSTANCE)
                                .build())
                        .build())
                .build();        // Create a RestTemplate that uses the custom HttpClient
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
        requestFactory.setHttpClient(httpClient);

        RestTemplate restTemplate = new RestTemplate(requestFactory);

        // Make a GET request to the /hello endpoint

        ResponseEntity<String> response = null;
        try {
            response = restTemplate.getForEntity("https://127.0.0.1:" + port + "/test/hello", String.class);


        } catch (Exception e) {
            e.printStackTrace();
        }

        Assertions.assertNotNull(response, "Response is null");
        assertThat(response.getBody()).isEqualTo("Hello, World!");
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
}