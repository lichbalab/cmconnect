package com.lichbalab.ksc;

import java.io.InputStream;
import java.io.OutputStream;
import java.security.cert.CertificateException;

import com.lichbalab.certificate.Certificate;
import org.bouncycastle.openssl.PEMException;

public interface DocSignService {
    /**
     *
     * @param doc The document to be signed
     * @param certificate Certificate for signature.
     * @return Signed document
     */
    OutputStream signPdf(InputStream doc, Certificate certificate) throws CertificateException, PEMException;

}
