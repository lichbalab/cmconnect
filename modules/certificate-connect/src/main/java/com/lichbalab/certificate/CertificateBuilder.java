package com.lichbalab.certificate;

import java.io.IOException;
import java.io.Reader;
import java.security.PrivateKey;

import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;

public class CertificateBuilder {

    public static Certificate buildFromPEM(Reader reader) throws IOException {
        X509CertificateHolder cert       = null;
        PrivateKey            privateKey = null;

        try (PEMParser pemParser = new PEMParser(reader)) {
            JcaPEMKeyConverter converter = new JcaPEMKeyConverter();
            Object object = pemParser.readObject();

            while (object != null) {
                if (object instanceof X509CertificateHolder x509Cert) {
                    cert = x509Cert;
                } else if (object instanceof PEMKeyPair keyPair) {
                    privateKey = converter.getPrivateKey(keyPair.getPrivateKeyInfo());
                } else if (object instanceof PrivateKeyInfo privateKeyInfo) {
                    privateKey = converter.getPrivateKey(privateKeyInfo);
                }
                object = pemParser.readObject();
            }
        }

        if (cert == null || privateKey == null) {
            throw new IllegalArgumentException("The provided PEM file must contain both a certificate and a private key.");
        }

        Certificate certificateObj = new Certificate();
        certificateObj.setCertificateData(cert.getEncoded());
        certificateObj.setPrivateKeyData(privateKey.getEncoded());
        certificateObj.setExpirationDate(cert.getNotAfter());
        certificateObj.setSubject(cert.getSubject().toString());
        certificateObj.setIssuer(cert.getIssuer().toString());
        certificateObj.setSerialNumber(cert.getSerialNumber());

        return certificateObj;
    }

}
