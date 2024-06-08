package com.lichbalab.cmc.sdk.test;

import com.lichbalab.cmc.core.exception.CmcRuntimeException;
import com.lichbalab.cmc.sdk.CmcClientConfig;
import com.lichbalab.cmc.sdk.client.CmcClient;
import com.lichbalab.cmc.sdk.client.CmcClientException;
import com.lichbalab.cmc.sdk.client.CmcClientFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
class CmcClientTest {

    static final Logger logger = LoggerFactory.getLogger(CmcClientTest.class);

    private static PostgreSQLContainer<?> POSTGRES_CONTAINER;
    private static GenericContainer<?>    CMC_API;
    private static CmcClientConfig        CMS_CONFIG;

    @BeforeAll
    public static void beforeAll() {
        Network network           = Network.newNetwork();
        int     API_EXPOSED_POPRT = 8080;

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

        CMS_CONFIG = new CmcClientConfig();
        CMS_CONFIG.setBaseUrl("http://localhost:" + CMC_API.getMappedPort(API_EXPOSED_POPRT));
        // Start containers and set system properties here
    }

    @AfterAll
    public static void afterAll() throws Exception {
        CMC_API.stop();
        POSTGRES_CONTAINER.stop();
    }

    @Test
    void testGetCertificateByAlias() {
        CmcClient cmcClient = CmcClientFactory.createService(CMS_CONFIG);
        String alias = "notExist";
        CmcClientException exception = null;
        try {
            cmcClient.getCertificateByAlias(alias);
        }catch (CmcClientException ex) {
            exception = ex;
        }
        Assertions.assertNotNull(exception, CmcClientException.class.getSimpleName() + " is expected, but actual exceptions is " + exception);
        Assertions.assertTrue(exception.getMessage().contains("No certificates found with alias=" + alias), "Wrong error message.");
    }

    @Test
    void testGetCertificates() {
        CmcClient cmcClient = CmcClientFactory.createService(CMS_CONFIG);
        Assertions.assertDoesNotThrow(cmcClient::getCertificates);
        Assertions.assertTrue(cmcClient.getCertificates().isEmpty());
    }
}