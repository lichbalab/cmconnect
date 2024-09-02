package com.lichbalab.cmc.spring.sdk.test;

import com.lichbalab.cmc.sdk.client.CmcClient;
import com.lichbalab.cmc.spring.sdk.CmcRestTemplateService;
import com.lichbalab.cmc.spring.sdk.SslBundleRegistrySynchronizer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.web.server.Ssl;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.util.List;
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