import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.lichbalab.certificate.Certificate;
import com.lichbalab.certificate.CertificateUtils;
import com.lichbalab.ksc.doc.DocSignService;
import com.lichbalab.ksc.doc.DocSignServiceImpl;
import eu.europa.esig.dss.model.DSSDocument;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class DocSignServiceTest {

    private final DocSignService signService = new DocSignServiceImpl();

    @Test
    void signPdfTest() throws IOException {
        File            signCertFile = new File("src/test/resources/certs/test.pem");
        Certificate     signCert     = CertificateUtils.buildFromPEM(new FileReader(signCertFile));
        FileInputStream doc          = new FileInputStream("src/test/resources/docs/test_doc_for_sign.pdf");

        DSSDocument signedDoc = signService.signPdf(doc, signCert);
        Assertions.assertNotNull(signedDoc, "Failed to sign pdf doc.");
    }
}