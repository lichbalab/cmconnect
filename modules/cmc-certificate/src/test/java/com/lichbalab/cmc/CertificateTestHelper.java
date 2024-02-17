package com.lichbalab.cmc;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;

import com.lichbalab.certificate.CertificateUtils;
import com.lichbalab.certificate.dto.CertificateDto;
import com.lichbalab.cmc.mapper.CertificateDtoMapper;
import com.lichbalab.cmc.model.Certificate;
import com.lichbalab.cmc.service.CertificateServiceIntegrationTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

public class CertificateTestHelper {

    public static List<CertificateDto> CERTS_DTO = getCertPaths().map(CertificateTestHelper::createTestCertificate).toList();
    public static CertificateDto createTestCertificate(Path path) {
        com.lichbalab.certificate.Certificate certPem;
        try {
            certPem = CertificateUtils.buildFromPEM(new FileReader(path.toFile()));
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

        return CertificateDtoMapper.certificateToDto(certPem, path.getFileName().toString());
    }
    public static Stream<Path> getCertPaths() {
        try {
            Path certDir = Paths.get(CertificateServiceIntegrationTest.class.getResource("/certs").toURI());
            return Files.list(certDir);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public static void cleanUpCertificates(TestEntityManager entityManager) {
        List<Certificate> allCerts = entityManager.getEntityManager().createQuery("SELECT c FROM Certificate c", Certificate.class).getResultList();
        allCerts.forEach(c -> entityManager.getEntityManager().remove(c));
    }
}