package com.lichbalab.ksc.service;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;

import com.lichbalab.certificate.CertificateBuilder;
import com.lichbalab.ksc.dto.CertificateDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;


@Testcontainers
@DataJpaTest(properties = { "spring.test.database.replace=none"})
@ComponentScan(basePackages = "com.lichbalab.ksc")
public class CertificateServiceIntegrationTest {

    private final Stream<Path> CERT_PATHS = getCertPaths();
    private static List<CertificateDto> CERTS;

    @BeforeAll
    static void prepareTests() {
        CERTS = getCertPaths().map(CertificateServiceIntegrationTest::createTestCertificate).toList();
    }

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
    void testCreateCertificate() throws Exception {
        List<CertificateDto> createdCerts = CERTS.stream().map(certificateService::createCertificate).toList();
        Assertions.assertEquals(CERTS, createdCerts, "Certificates creation results are unexpected");
    }

    @Test
    public void testGetAllCertificates() throws Exception {
        // Create a few test certificates
        CERTS.forEach(certificateService::createCertificate);        // Set properties for dtos
        
        List<CertificateDto> certificates = certificateService.getAllCertificates();
        
        Assertions.assertEquals(certificates.size(), CERTS.size(), "Getting all certificates works unexpected.");
    }

    //@Test
    public void testGetCertificateById() throws Exception {
        //CertificateDto dto = createTestCertificate();
        // Set properties for dto
        
        //CertificateDto created = certificateService.createCertificate(dto);
        //CertificateDto retrieved = certificateService.getCertificateById(created.getId());
        
        //assertThat(retrieved).isEqualTo(created);
    }

    @Test
    public void testUpdateCertificate() throws Exception {
        CertificateDto created = certificateService.createCertificate(CERTS.get(0));
        
        // Update some properties of created
        CertificateDto updated = certificateService.updateCertificate(created.getId(), created);
        
        assertThat(updated).isNotNull();
        // Assert updated properties
    }

    @Test
    public void testDeleteCertificate() throws Exception {

        CertificateDto created = certificateService.createCertificate(CERTS.get(0));
        certificateService.deleteCertificate(created.getId());
        
        CertificateDto retrieved = certificateService.getCertificateById(created.getId());
        assertThat(retrieved).isNull();
    }

    private static CertificateDto createTestCertificate(Path path) {
        //Path                                  path    = Paths.get(Objects.requireNonNull(CertificateServiceIntegrationTest.class.getResource("/test.pem")).toURI());
        com.lichbalab.certificate.Certificate certPem = null;
        try {
            certPem = CertificateBuilder.buildFromPEM(new FileReader(path.toFile()));
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

/*
        ClassLoader classLoader = CertificateServiceIntegrationTest.class.getClassLoader();
        Object         resourceURL = CertificateServiceIntegrationTest.class.getResource("/certs");
*/

        CertificateDto testCertificate = new CertificateDto();
        testCertificate.setExpirationDate(certPem.getExpirationDate());
        testCertificate.setSerialNumber(certPem.getSerialNumber());
        testCertificate.setExpirationDate(certPem.getExpirationDate());
        testCertificate.setSubject(certPem.getSubject());
        testCertificate.setIssuer(certPem.getIssuer());
        testCertificate.setCertificateData(certPem.getCertificateData());
        testCertificate.setPrivateKeyData(certPem.getPrivateKeyData());

        return testCertificate;
    }

    private static Stream<Path> getCertPaths() {
        try {
            Path certDir = Paths.get(CertificateServiceIntegrationTest.class.getResource("/certs").toURI());
            return Files.list(certDir);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}