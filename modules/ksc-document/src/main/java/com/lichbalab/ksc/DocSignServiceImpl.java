package com.lichbalab.ksc;

import java.io.InputStream;
import java.io.OutputStream;

import com.lichbalab.certificate.Certificate;
import com.lichbalab.certificate.CertificateUtils;
import eu.europa.esig.dss.enumerations.DigestAlgorithm;
import eu.europa.esig.dss.enumerations.MimeTypeEnum;
import eu.europa.esig.dss.enumerations.SignatureLevel;
import eu.europa.esig.dss.model.DSSDocument;
import eu.europa.esig.dss.model.InMemoryDocument;
import eu.europa.esig.dss.model.SignatureValue;
import eu.europa.esig.dss.model.ToBeSigned;
import eu.europa.esig.dss.pades.PAdESSignatureParameters;
import eu.europa.esig.dss.pades.signature.PAdESService;
import eu.europa.esig.dss.token.DSSPrivateKeyEntry;
import eu.europa.esig.dss.token.Pkcs12SignatureToken;
import eu.europa.esig.dss.token.SignatureTokenConnection;
import eu.europa.esig.dss.validation.CommonCertificateVerifier;
import org.bouncycastle.openssl.PEMException;

public class DocSignServiceImpl implements DocSignService {

        @Override
        public OutputStream signPdf(InputStream doc, Certificate certificate) throws PEMException {

                // Get a token connection based on a pkcs12 file commonly used to store private
                // keys with accompanying public key certificates, protected with a password-based
                // symmetric key -
                // Return AbstractSignatureTokenConnection signingToken

                // and it's first private key entry from the PKCS12 store
                // Return DSSPrivateKeyEntry privateKey *****

                try (SignatureTokenConnection signingToken = new Pkcs12SignatureToken(CertificateUtils.generatePKCS12Keystore(certificate), null)) {
                        DSSPrivateKeyEntry privateKey = signingToken.getKeys().get(0);

                        // tag::demo[]
                        // import eu.europa.esig.dss.pades.PAdESSignatureParameters;
                        // import eu.europa.esig.dss.enumerations.SignatureLevel;
                        // import eu.europa.esig.dss.enumerations.DigestAlgorithm;
                        // import eu.europa.esig.dss.validation.CommonCertificateVerifier;
                        // import eu.europa.esig.dss.pades.signature.PAdESService;
                        // import eu.europa.esig.dss.model.ToBeSigned;
                        // import eu.europa.esig.dss.model.SignatureValue;
                        // import eu.europa.esig.dss.model.DSSDocument;

                        // Preparing parameters for the PAdES signature
                        PAdESSignatureParameters parameters = new PAdESSignatureParameters();
                        // We choose the level of the signature (-B, -T, -LT, -LTA).
                        parameters.setSignatureLevel(SignatureLevel.PAdES_BASELINE_B);
                        // We set the digest algorithm to use with the signature algorithm. You must use the
                        // same parameter when you invoke the method sign on the token. The default value is
                        // SHA256
                        parameters.setDigestAlgorithm(DigestAlgorithm.SHA256);

                        // We set the signing certificate
                        //parameters.setSigningCertificate(privateKey.getCertificate());
                        parameters.setSigningCertificate(SignUtil.getDssCertificateToken(certificate));

                        // We set the certificate chain
                        parameters.setCertificateChain(SignUtil.getDssCertificateChain(certificate));

                        // Create common certificate verifier
                        CommonCertificateVerifier commonCertificateVerifier = new CommonCertificateVerifier();
                        // Create PAdESService for signature
                        PAdESService service = new PAdESService(commonCertificateVerifier);

                        // Get the SignedInfo segment that need to be signed.
                        InMemoryDocument toSignDocument = new InMemoryDocument(doc, null, MimeTypeEnum.PDF);
                        ToBeSigned       dataToSign     = service.getDataToSign(toSignDocument, parameters);

                        // This function obtains the signature value for signed information using the
                        // private key and specified algorithm
                        DigestAlgorithm digestAlgorithm = parameters.getDigestAlgorithm();
                        SignatureValue  signatureValue  = signingToken.sign(dataToSign, digestAlgorithm, privateKey);

                        // Optionally or for debug purpose :
                        // Validate the signature value against the original dataToSign
                        //assertTrue(service.isValidSignatureValue(dataToSign, signatureValue, privateKey.getCertificate()));

                        // We invoke the padesService to sign the document with the signature value obtained in
                        // the previous step.
                        DSSDocument signedDocument = service.signDocument(toSignDocument, parameters, signatureValue);


                        return null;
                } catch (Exception e) {
                        throw new RuntimeException(e);
                }

        }
}
