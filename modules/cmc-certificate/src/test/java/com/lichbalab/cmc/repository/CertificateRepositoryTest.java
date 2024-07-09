package com.lichbalab.cmc.repository;

import java.util.List;
import java.util.Optional;

import com.lichbalab.certificate.CertificateTestHelper;
import com.lichbalab.cmc.mapper.CertificateMapper;
import com.lichbalab.cmc.model.Certificate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.annotation.Commit;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@DataJpaTest(properties = { "spring.test.database.replace=none"})
@ComponentScan(basePackages = "com.lichbalab.cmc")
public class CertificateRepositoryTest {
    @Container
    public static PostgreSQLContainer<?> databaseContainer = getDbContainer()
             .withDatabaseName("test")
             .withUsername("test")
             .withPassword("test");

    @Autowired
    private CertificateMapper mapper;

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private CertificateRepository certificateRepository;

    private static List<Certificate> testCertificates;

    @DynamicPropertySource
    public static void setDataSourceProperties(DynamicPropertyRegistry registry) {
        registry.add("DB_HOST", databaseContainer::getHost);
        registry.add("DB_PORT", databaseContainer::getFirstMappedPort);
        registry.add("DB_NAME", databaseContainer::getDatabaseName);
        registry.add("DB_USERNAME", databaseContainer::getUsername);
        registry.add("DB_PASSWORD", databaseContainer::getPassword);
    }

    @BeforeEach
    public void setUp() throws Exception {
        testCertificates = CertificateTestHelper.CERTS_DTO.stream().map(mapper::dtoToDom).toList();
        Assertions.assertFalse(testCertificates.isEmpty(), "Test certificates list is empty");
    }

    @AfterEach
    public void tearDown() {
        cleanUpCertificates(entityManager);
    }

    @Test
    @Commit
    void testSaveCertificate() {
        List<Certificate> savedCertificates = testCertificates.stream().map(certificateRepository::save).toList();
        Assertions.assertEquals(testCertificates.size(), savedCertificates.size(), "Certificates saving results are unexpected");
        Assertions.assertEquals(testCertificates, savedCertificates, "Certificates saving results are unexpected");
    }

    @Test
    @Commit
    void testFindAllCertificates() {
        testCertificates.forEach(entityManager::persistAndFlush);
        List<Certificate> foundCertificates = certificateRepository.findAll();
        Assertions.assertEquals(testCertificates.size(), foundCertificates.size(), "Getting all certificates works unexpected.");
        Assertions.assertEquals(testCertificates, foundCertificates, "Getting all certificates works unexpected.");
    }

    @Test
    @Commit
    void testFindCertificateById() {
        List<Certificate> savedCertificates = testCertificates.stream().map(entityManager::persistAndFlush).toList();

        for (Certificate certificate : savedCertificates) {
            Optional<Certificate> foundCertificate = certificateRepository.findById(certificate.getId());
            Assertions.assertTrue(foundCertificate.isPresent(), "Certificate not found by id.");
            Assertions.assertEquals(certificate, foundCertificate.get(), "Wrong certificate found by id.");
        }
    }

    @Test
    @Commit
    void testDeleteCertificateById() {
        List<Certificate> savedCertificates = testCertificates.stream().map(entityManager::persistAndFlush).toList();

       for (Certificate certificate : savedCertificates) {
           certificateRepository.deleteById(certificate.getId());
           Certificate foundCertificate = entityManager.find(Certificate.class, certificate.getId());
           Assertions.assertNull(foundCertificate, "Certificate is not deleted.");
       }
    }

    private static PostgreSQLContainer<?> getDbContainer() {
        try (PostgreSQLContainer<?> databaseContainer = new PostgreSQLContainer<>("postgres:16-alpine")){
            return databaseContainer;
        }
    }

    public static void cleanUpCertificates(TestEntityManager entityManager) {
        List<Certificate> allCerts = entityManager.getEntityManager().createQuery("SELECT c FROM Certificate c", Certificate.class).getResultList();
        allCerts.forEach(c -> entityManager.getEntityManager().remove(c));
    }
}