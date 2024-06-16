package com.lichbalab.cms.spring.sdk.test;

import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.ssl.NoopHostnameVerifier;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactoryBuilder;
import org.apache.hc.client5.http.ssl.TrustAllStrategy;
import org.apache.hc.core5.ssl.SSLContextBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = TestApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@ComponentScan(basePackages = "com.lichbalab.cmc.spring.sdk.test") // Scan the test package
//@ContextConfiguration(classes = {SslConfig.class, MyTomcatWebServerCustomizer.class}) // Import the SSL config and the test controller
public class CmcSpringSdkIT {

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
