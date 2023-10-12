package by.cyberveska.ksc.repository;

import java.io.ByteArrayInputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Objects;
import java.util.Optional;

import by.cyberveska.ksc.model.Certificate;
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
    public static PostgreSQLContainer<?> databaseContainer = new PostgreSQLContainer<>("postgres:latest")
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
        Path path = Paths.get(Objects.requireNonNull(CertificateRepositoryTest.class.getResource("/test.pem")).toURI());
        byte[] pemData = java.nio.file.Files.readAllBytes(path);
        CertificateFactory factory = CertificateFactory.getInstance("X.509");
        X509Certificate cert = (X509Certificate) factory.generateCertificate(new ByteArrayInputStream(pemData));

        testCertificate = new Certificate();
        testCertificate.setExpirationDate(cert.getNotAfter());
        testCertificate.setSubject(cert.getSubjectDN().getName());
        testCertificate.setIssuer(cert.getIssuerDN().getName());
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
        entityManager.persist(testCertificate);
        entityManager.flush();

        assertThat(certificateRepository.findAll()).hasSize(1);
    }

    @Test
    void testFindCertificateById() {
        Certificate certificate = entityManager.persistAndFlush(testCertificate);

        Optional<Certificate> foundCertificate = certificateRepository.findById(certificate.getId());
        assertThat(foundCertificate).isPresent();
        assertThat(foundCertificate.get().getId()).isEqualTo(certificate.getId());
    }

    @Test
    void testDeleteCertificateById() {
        Certificate certificate = entityManager.persistAndFlush(testCertificate);

        certificateRepository.deleteById(certificate.getId());
        Optional<Certificate> foundCertificate = certificateRepository.findById(certificate.getId());
        assertThat(foundCertificate.isPresent()).isFalse();
    }
}