package com.lichbalab.ksc.service;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import com.lichbalab.certificate.CertificateBuilder;
import com.lichbalab.ksc.dto.CertificateDto;
import org.junit.Ignore;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;


@Testcontainers
//@Import({CertificateService.class, CertificateMapper.class, CertificateRepository.class})
//@RunWith(SpringRunner.class)
//@DataJpaTest(properties = { "spring.test.database.replace=none"})
//@SpringBootTest
//@ContextConfiguration(classes = {CertificateService.class})
@DataJpaTest(properties = { "spring.test.database.replace=none"})
//@EnableAutoConfiguration
@ComponentScan(basePackages = "com.lichbalab.ksc")
public class CertificateServiceIntegrationTest {

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
    public void testCreateCertificate() throws Exception {
        CertificateDto dto = createTestCertificate();
        // Set properties for dto

        CertificateDto created = certificateService.createCertificate(dto);
        
        assertThat(created).isNotNull();
        // Assert other properties as necessary
    }

    @Test
    public void testGetAllCertificates() throws Exception {
        // Create a few test certificates
        CertificateDto dto1 = createTestCertificate();
        CertificateDto dto2 = createTestCertificate();
        // Set properties for dtos
        
        certificateService.createCertificate(dto1);
        certificateService.createCertificate(dto2);
        
        List<CertificateDto> certificates = certificateService.getAllCertificates();
        
        assertThat(certificates).hasSize(2);
    }

    //@Test
    public void testGetCertificateById() throws Exception {
        CertificateDto dto = createTestCertificate();
        // Set properties for dto
        
        CertificateDto created = certificateService.createCertificate(dto);
        CertificateDto retrieved = certificateService.getCertificateById(created.getId());
        
        assertThat(retrieved).isEqualTo(created);
    }

    @Test
    public void testUpdateCertificate() throws Exception {
        CertificateDto dto = createTestCertificate();
        // Set properties for dto
        
        CertificateDto created = certificateService.createCertificate(dto);
        
        // Update some properties of created
        CertificateDto updated = certificateService.updateCertificate(created.getId(), created);
        
        assertThat(updated).isNotNull();
        // Assert updated properties
    }

    @Test
    public void testDeleteCertificate() throws Exception {
        CertificateDto dto = createTestCertificate();
        // Set properties for dto
        
        CertificateDto created = certificateService.createCertificate(dto);
        certificateService.deleteCertificate(created.getId());
        
        CertificateDto retrieved = certificateService.getCertificateById(created.getId());
        assertThat(retrieved).isNull();
    }

    private static CertificateDto createTestCertificate() throws Exception {
        Path                                  path    = Paths.get(Objects.requireNonNull(CertificateServiceIntegrationTest.class.getResource("/test.pem")).toURI());
        com.lichbalab.certificate.Certificate certPem = CertificateBuilder.buildFromPEM(new FileReader(path.toFile()));

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

/*
    private Stream<Path> getCertPaths() {

        try {
            ClassLoader classLoader = CertificateServiceIntegrationTest.class.getClassLoader();
            Object         resourceURL = classLoader.getResource("test/resources/certs");
            Path        certDir     = Paths.get(Objects.requireNonNull(CertificateServiceIntegrationTest.class.getResource("/certs")).toURI());
            return Files.list(certDir);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }

    }
*/
}