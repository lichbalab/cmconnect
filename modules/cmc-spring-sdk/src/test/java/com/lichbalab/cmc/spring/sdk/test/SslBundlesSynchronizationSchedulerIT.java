package com.lichbalab.cmc.spring.sdk.test;

import org.junit.jupiter.api.Test;
import org.springframework.boot.ssl.SslBundle;
import org.springframework.boot.web.context.WebServerApplicationContext;
import org.springframework.boot.web.server.Ssl;
import org.springframework.web.client.RestTemplate;

import java.util.List;

public class SslBundlesSynchronizationSchedulerIT extends BaseIntegrationTest {

    @Override
    protected int getPort() {
        return ((WebServerApplicationContext) SpringContextUtil.getContext()).getWebServer().getPort();
    }

    @Test
    public void testScheduledSynchronization() throws InterruptedException {
        before(Ssl.ClientAuth.NEED.name(), CertConfig.ALIAS_2, "*/10 * * * * *");
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

        // SSL handshake should fail as the client certificate is not added
        callApiAndCheckHandshakeException(restTemplate);

        CERTS.stream()
                .filter(cert -> CertConfig.CERT_ALAIS_3.equals(cert.getAlias()))
                .forEach(cert -> cmcClient.addCertificate(cert));

        // Update SSL context for the REST API endpoint with client certificate with alias "lichbalab3.key"
        Thread.sleep(15000);
        callApiAndCheckResponse(restTemplate);

        CERTS.forEach(cert -> cmcClient.deleteCertificate(CertConfig.CERT_ALAIS_3));
        Thread.sleep(15000);

        callApiAndCheckHandshakeException(restTemplate);
    }

    public void before(String clientAuth, String keyAlias, String cron) {
        before(
                "--TEST_CMC_API_PORT=" + CMC_API.getMappedPort(API_EXPOSED_PORT),
                "--TEST_CMC_CLIENT_AUTH=" + clientAuth,
                "--TEST_KEY_ALIAS=" + keyAlias,
                "--TEST_CMC_CRON=" + cron
        );
    }
}