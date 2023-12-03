package com.lichbalab.certificate;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Reader;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.openssl.PEMException;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;

public class CertificateUtils {

    /**
     * Builds @{@link Certificate} instance from PEM file.
     *
     * @param reader @{@link Reader} which is build using PEM file bytes.
     * @return certificate instance
     * @throws IOException Thrown in case of error while parsing PEM file.
     */
    public static Certificate buildFromPEM(Reader reader) throws IOException {
        PrivateKey                  privateKey = null;
        List<X509CertificateHolder> certChain  = new ArrayList<>();

        try (PEMParser pemParser = new PEMParser(reader)) {
            JcaPEMKeyConverter converter = new JcaPEMKeyConverter();
            Object object = pemParser.readObject();

            while (object != null) {
                if (object instanceof X509CertificateHolder x509Cert) {
                    certChain.add(x509Cert);
                } else if (object instanceof PEMKeyPair keyPair) {
                    privateKey = converter.getPrivateKey(keyPair.getPrivateKeyInfo());
                } else if (object instanceof PrivateKeyInfo privateKeyInfo) {
                    privateKey = converter.getPrivateKey(privateKeyInfo);
                }
                object = pemParser.readObject();
            }
        }

        if (certChain.isEmpty() || privateKey == null) {
            throw new IllegalArgumentException("The provided PEM file must contain at least a certificate or a private key.");
        }

        X509CertificateHolder cert = certChain.get(0);

        Certificate certificateObj = new Certificate();
        certificateObj.setCertificateChainData(CertificateUtils.certChainToByteArray(certChain));
        certificateObj.setPrivateKeyData(privateKey.getEncoded());
        certificateObj.setExpirationDate(cert.getNotAfter());
        certificateObj.setSubject(cert.getSubject().toString());
        certificateObj.setIssuer(cert.getIssuer().toString());
        certificateObj.setSerialNumber(cert.getSerialNumber());
        certificateObj.setCertChain(certChain);

        return certificateObj;
    }

    /**
     * Converts certificate chain into byte array.
     *
     * @param certChain Certificate chain, list of @{@link X509CertificateHolder}
     *
     * @return Byte array
     * @throws IOException In case of converting a certificate to bytes.
     */
    public static byte[] certChainToByteArray(List<X509CertificateHolder> certChain) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        for (X509CertificateHolder certHolder : certChain) {
            baos.write(certHolder.getEncoded());
        }

        return baos.toByteArray();
    }

    /**
     * Converts byte array into certificate chain
     *
     * @param certChainData Byte array
     * @return List of @{@link X509CertificateHolder}
     */
    public static List<X509CertificateHolder> byteArrayToCertChain(byte[] certChainData) throws IOException {
        List<X509CertificateHolder> certChain = new ArrayList<>();

        ByteArrayInputStream bais = new ByteArrayInputStream(certChainData);
        try (ASN1InputStream asn1InputStream = new ASN1InputStream(bais)) {
            while (bais.available() > 0) {
                ASN1Sequence          seq        = (ASN1Sequence) asn1InputStream.readObject();
                X509CertificateHolder certHolder = new X509CertificateHolder(seq.getEncoded());
                certChain.add(certHolder);
            }
        }

        return certChain;
    }

    public static PrivateKey convertBytesToPrivateKey(byte[] privateKeyBytes) throws PEMException {
        PrivateKeyInfo privateKeyInfo = PrivateKeyInfo.getInstance(privateKeyBytes);

        // Convert to PrivateKey
        JcaPEMKeyConverter converter = new JcaPEMKeyConverter().setProvider("BC");
        return converter.getPrivateKey(privateKeyInfo);
    }

    public static byte[] generatePKCS12Keystore(Certificate certificate) throws Exception {
        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        keyStore.load(null, null); // Initialize the keystore
        PrivateKey privateKey = convertBytesToPrivateKey(certificate.getPrivateKeyData());


        java.security.cert.Certificate[] certificateChain = getCertificateChain(certificate);
        keyStore.setKeyEntry("alias", privateKey, null, certificateChain);


        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        keyStore.store(bos, null);
        return bos.toByteArray();
    }

    private static X509Certificate getFromCertHolder(X509CertificateHolder certificateHolder) {
        JcaX509CertificateConverter converter = new JcaX509CertificateConverter();

        try {
            return converter.getCertificate(certificateHolder);
        } catch (CertificateException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static X509Certificate[] getCertificateChain(Certificate certificate) {
        List<X509Certificate> certList = certificate.getCertChain().stream().map(CertificateUtils::getFromCertHolder).toList();
        return certList.toArray(new X509Certificate[]{});
    }


}