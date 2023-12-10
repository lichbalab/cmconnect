package com.lichbalab.ksc.doc;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.cert.CertificateException;

import com.lichbalab.certificate.Certificate;
import eu.europa.esig.dss.model.DSSDocument;
import org.bouncycastle.openssl.PEMException;

public interface DocSignService {
    /**
     *
     * @param doc The document to be signed
     * @param certificate Certificate for signature.
     * @return Signed document
     */
    DSSDocument signPdf(InputStream doc, String certAlias) throws IOException;

}
