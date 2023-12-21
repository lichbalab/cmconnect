package com.lichbalab.ksc.service;

import java.util.List;

import com.lichbalab.ksc.CertificateTestHelper;
import com.lichbalab.ksc.dto.CertificateDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;


@Testcontainers
@DataJpaTest(properties = {"spring.test.database.replace=none"})
@ComponentScan(basePackages = "com.lichbalab.ksc")
public class CertificateServiceIntegrationTest {
    private final static List<CertificateDto> CERTS = CertificateTestHelper.CERTS_DTO;

    @Container
    public static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:latest")
             .withDatabaseName("test")
             .withUsername("user")
             .withPassword("password");

    @Autowired
    private CertificateService certificateService;

    @DynamicPropertySource
    public static void setDataSourceProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
    }


    @Test
    void testCreateCertificate() {
        List<CertificateDto> createdCerts = CERTS.stream().map(certificateService::createCertificate).toList();

        Assertions.assertEquals(CERTS, createdCerts, "Certificates creation results are unexpected");
    }

    @Test
    void testGetAllCertificates() {
        // Create a few test certificates
        CERTS.forEach(certificateService::createCertificate);

        List<CertificateDto> certificates = certificateService.getAllCertificates();

        Assertions.assertEquals(certificates.size(), CERTS.size(), "Getting all certificates works unexpected.");
        Assertions.assertEquals(CERTS, certificates, "Getting all certificates works unexpected.");
    }

    @Test
    void testGetCertificateById() {
        CertificateDto created   = certificateService.createCertificate(CERTS.get(0));
        CertificateDto retrieved = certificateService.getCertificateById(created.getId());

        Assertions.assertEquals(created, retrieved, "Wrong certificate found by id.");
    }

    @Test
    void testUpdateCertificate() throws Exception {
        CertificateDto created = certificateService.createCertificate(CERTS.get(0));

        CertificateDto updated = certificateService.updateCertificate(created.getId(), created);

        Assertions.assertEquals(created, updated, "Certificate is not updated correctly.");
    }

    @Test
    void testDeleteCertificate() {
        CertificateDto created = certificateService.createCertificate(CERTS.get(0));
        certificateService.deleteCertificate(created.getId());

        CertificateDto retrieved = certificateService.getCertificateById(created.getId());
        Assertions.assertNull(retrieved, "Certificate has not been deleted.");
    }

}