package com.lichbalab.ksc;

import java.io.InputStream;
import java.io.OutputStream;
import java.security.cert.Certificate;

public interface DocSignService {
    /**
     *
     * @param doc The document to be signed
     * @param certificate Certificate for signature.
     * @return Signed document
     */
    OutputStream sign(InputStream doc, Certificate certificate);

}
