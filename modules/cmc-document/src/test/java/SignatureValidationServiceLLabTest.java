import com.lichbalab.certificate.Certificate;
import com.lichbalab.certificate.CertificateUtils;
import com.lichbalab.cmc.doc.DocSignService;
import com.lichbalab.cmc.doc.DocSignServiceImpl;
import com.lichbalab.cmc.doc.SignatureValidationServiceLLab;
import com.lichbalab.cmc.doc.SignatureValidationServiceLLabImpl;
import com.lichbalab.cmc.mapper.CertificateDtoMapper;
import eu.europa.esig.dss.model.DSSDocument;
import eu.europa.esig.dss.ws.validation.dto.WSReportsDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;

public class SignatureValidationServiceLLabTest {


    @Test
    void verifySignature() throws IOException {
        File signCertFile = new File("src/test/resources/certs/test.pem");
        Certificate signCert = CertificateUtils.buildFromPEM(new FileReader(signCertFile));
        FileInputStream doc = new FileInputStream("src/test/resources/docs/test_doc_for_sign.pdf");
        CertificateServiceTest certService = new CertificateServiceTest(CertificateDtoMapper.certificateToDto(signCert, "alias"));
        DocSignService signService = new DocSignServiceImpl(certService);
        DSSDocument signedDoc = signService.signPdf(doc, "alias");

        SignatureValidationServiceLLab signatureValidationServiceLLab = new SignatureValidationServiceLLabImpl();
        WSReportsDTO report = null;
        try (InputStream is = signedDoc.openStream()) {
            report = signatureValidationServiceLLab.validateSignature(is.readAllBytes());
        }

        Assertions.assertNotNull(report, "Failed to verify signature.");
    }
}