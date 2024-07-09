package com.lichbalab.certificate;

import com.lichbalab.certificate.dto.CertificateDto;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;

public class CertificateTestHelper {

    public static List<CertificateDto> CERTS_DTO = getCertPaths().map(CertificateTestHelper::createTestDtoCertificate).toList();

    public static List<Certificate> CERTS = getCertPaths().map(CertificateTestHelper::createTestCertificate).toList();

    public static CertificateDto createTestDtoCertificate(Path path) {
        return CertificateUtils.addToDto(createTestCertificate(path));
    }

    public static Certificate createTestCertificate(Path path) {
        com.lichbalab.certificate.Certificate certPem;
        try {
            certPem = CertificateUtils.buildFromPEM(new FileReader(path.toFile()));
            certPem.setAlias(path.getFileName().toString());
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

        return certPem;
    }

    public static Stream<Path> getCertPaths() {
        try {
            Path certDir = Paths.get(CertificateTestHelper.class.getResource("/certs").toURI());
            return Files.list(certDir);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}