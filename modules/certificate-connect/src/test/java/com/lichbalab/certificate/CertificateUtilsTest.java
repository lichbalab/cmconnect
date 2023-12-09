package com.lichbalab.certificate;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;

import org.bouncycastle.cert.X509CertificateHolder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class CertificateUtilsTest {

    @Test
    void byteArrayToCertChainTest() throws IOException {
        List<Certificate> certs = getCertPaths().stream().map(CertificateUtilsTest::createTestCertificate).toList();
        for (Certificate cert : certs) {
            List<X509CertificateHolder> x509Certs = CertificateUtils.byteArrayToCertChain(cert.getCertificateChainData());
            Assertions.assertFalse(x509Certs.isEmpty(), "Certificate is not deserialized correctly from byte array.");
            Assertions.assertEquals(cert.getSerialNumber(), x509Certs.get(0).getSerialNumber(), "Certificate is not deserialized correctly from byte array.");
        }
    }

    private static List<Path> getCertPaths() {
        try (Stream<Path> streamPath = Files.list(Paths.get(CertificateUtilsTest.class.getResource("/certs").toURI()))) {
            return streamPath.toList();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private static Certificate createTestCertificate(Path path) {
        try {
            return CertificateUtils.buildFromPEM(new FileReader(path.toFile()));
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
}
