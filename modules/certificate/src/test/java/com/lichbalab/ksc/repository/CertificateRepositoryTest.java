package com.lichbalab.ksc.repository;

import java.io.FileReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.Optional;

import com.lichbalab.certificate.CertificateUtils;
import com.lichbalab.ksc.model.Certificate;
import jakarta.persistence.EntityManager;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@DataJpaTest(properties = { "spring.test.database.replace=none"})
public class CertificateRepositoryTest {

    @Container
    public static PostgreSQLContainer<?> databaseContainer = getDbContainer()
             .withDatabaseName("test")
             .withUsername("test")
             .withPassword("test");

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private CertificateRepository certificateRepository;

    private static Certificate testCertificate;

    @DynamicPropertySource
    public static void setDataSourceProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", databaseContainer::getJdbcUrl);
        registry.add("spring.datasource.username", databaseContainer::getUsername);
        registry.add("spring.datasource.password", databaseContainer::getPassword);
    }

    @BeforeEach
    public void setUp() throws Exception {
        // Read the certificate from the provided PEM file
        Path                                  path    = Paths.get(Objects.requireNonNull(CertificateRepositoryTest.class.getResource("/certs/test.pem")).toURI());
        com.lichbalab.certificate.Certificate certPem = CertificateUtils.buildFromPEM(new FileReader(path.toFile()));

        testCertificate = new Certificate();
        testCertificate.setExpirationDate(certPem.getExpirationDate());
        //testCertificate.setSerialNumber(certPem.getSerialNumber());
        testCertificate.setExpirationDate(certPem.getExpirationDate());
        testCertificate.setSubject(certPem.getSubject());
        testCertificate.setIssuer(certPem.getIssuer());
        testCertificate.setCertificateChainData(certPem.getCertificateChainData());
        testCertificate.setPrivateKeyData(certPem.getPrivateKeyData());
    }

    @AfterEach
    public void tearDown() {
        // Clean up resources, if needed
    }

    @Test
    void testSaveCertificate() {
        Certificate certificate = certificateRepository.save(testCertificate);
        assertThat(certificate.getId()).isNotNull();
    }

    @Test
    void testFindAllCertificates() {
        entityManager.persistAndFlush(testCertificate);
        assertThat(certificateRepository.findAll()).hasSize(1);
    }

    @Test
    void testFindCertificateById() {
        Certificate certificate = certificateRepository.save(testCertificate);

        Optional<Certificate> foundCertificate = certificateRepository.findById(certificate.getId());
        Assertions.assertThat(foundCertificate).isPresent();
        assertThat(foundCertificate.get().getId()).isEqualTo(certificate.getId());
    }

    @Test
    void testDeleteCertificateById() {
        Certificate certificate = entityManager.persist(testCertificate);

        certificateRepository.deleteById(certificate.getId());
        Optional<Certificate> foundCertificate = certificateRepository.findById(certificate.getId());
        Assertions.assertThat(foundCertificate).isNotPresent();
    }

    private static PostgreSQLContainer<?> getDbContainer() {
        try (PostgreSQLContainer<?> databaseContainer = new PostgreSQLContainer<>("postgres:latest")){
            return databaseContainer;
        }
    }

}