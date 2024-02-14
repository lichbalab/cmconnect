package com.lichbalab.cmc.controller;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;

import com.lichbalab.certificate.CertificateUtils;
import com.lichbalab.cmc.dto.CertificateDto;
import com.lichbalab.cmc.service.CertificateService;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static io.restassured.RestAssured.given;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
public class CertificateControllerIntegrationTest {

    @LocalServerPort
    private Integer port;

    private final static List<Path> CERT_PATH = getCertPaths();

    private final static List<CertificateDto> CERTS = CERT_PATH.stream().map(CertificateControllerIntegrationTest::createTestCertificate).toList();

    @Autowired
    private CertificateService certificateService;

    @DynamicPropertySource
    public static void setDataSourceProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
    }

    @Container
    public static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:16-alpine")
             .withDatabaseName("test")
             .withUsername("test")
             .withPassword("test");

    @BeforeEach
    public void setUp() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;
    }

    @Test
    void testCreateCertificate() {
        CertificateDto certificate = new CertificateDto();
        // ... set properties for the certificate DTO ...

        Response response = given()
                 .contentType("application/json")
                 .body(certificate)
                 .when()
                 .post("/certificates");

        Assertions.assertEquals(201, response.statusCode());  // Assuming 201 is the HTTP status for successful creation
        // ... any other assertions based on the expected response ...    }
    }
    @Test
    void testUploadCertificateFromFile() {
        // Assuming you have a file to upload, adjust accordingly
        File   fileToUpload = new File(CERT_PATH.get(0).toUri());
        String alias        = "testAlias";

        Response response = given()
                 .multiPart("file", fileToUpload)
                 .param("alias", alias)
                 .when()
                 .post("/certificates/upload");

        Assertions.assertEquals(201, response.statusCode());
    }

    @Test
    void testGetAllCertificates() {
        Response response = given()
                 .when()
                 .get("/certificates");

        Assertions.assertEquals(200, response.statusCode());
        // Add more assertions based on expected response content
    }

    @Test
    void testGetCertificateById() {
        // Assuming a certificate with ID 1 exists in the database for the test
        List<CertificateDto> createdCerts = CERTS.stream().map(certificateService::createCertificate).toList();
        for (CertificateDto cert : createdCerts) {
            Response response = given()
                     .when()
                     .get("/certificates/" + cert.getId());

            Assertions.assertEquals(200, response.statusCode());
        }
    }

    @Test
    void testUpdateCertificate() {
        List<CertificateDto> createdCerts = CERTS.stream().map(certificateService::createCertificate).toList();
        for (CertificateDto cert : createdCerts) {
            Response response = given()
                     .contentType("application/json")
                     .body(cert)
                     .when()
                     .put("/certificates/" + cert.getId());

            Assertions.assertEquals(200, response.statusCode());
        }
   }

    @Test
     void testDeleteCertificate() {
        List<CertificateDto> createdCerts = CERTS.stream().map(certificateService::createCertificate).toList();
        for (CertificateDto cert : createdCerts) {
            Response response = given()
                     .when()
                     .delete("/certificates/" + cert.getId());

            Assertions.assertEquals(200, response.statusCode());
        }
    }

    private static List<Path> getCertPaths() {
        try (Stream<Path> streamPath = Files.list(Paths.get(CertificateControllerIntegrationTest.class.getResource("/certs").toURI()))) {
            return streamPath.toList();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private static CertificateDto createTestCertificate(Path path) {
        com.lichbalab.certificate.Certificate certPem = null;
        try {
            certPem = CertificateUtils.buildFromPEM(new FileReader(path.toFile()));
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

        CertificateDto testCertificate = new CertificateDto();
        testCertificate.setExpirationDate(certPem.getExpirationDate());
        testCertificate.setSerialNumber(certPem.getSerialNumber());
        testCertificate.setExpirationDate(certPem.getExpirationDate());
        testCertificate.setSubject(certPem.getSubject());
        testCertificate.setIssuer(certPem.getIssuer());
        testCertificate.setCertificateChainData(certPem.getCertificateChainData());
        testCertificate.setPrivateKeyData(certPem.getPrivateKeyData());

        return testCertificate;
    }
}