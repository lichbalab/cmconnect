package by.cyberveska.ksc.repository;

import java.io.FileReader;
import java.math.BigInteger;
import java.nio.file.Path;
import java.security.PrivateKey;
import java.util.Date;

import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;

public class CertificateBuilder {

    public static Certificate buildFromPEM(Path pemPath) throws Exception {
        X509CertificateHolder cert       = null;
        PrivateKey            privateKey = null;

        try (PEMParser pemParser = new PEMParser(new FileReader(pemPath.toFile()))) {
            JcaPEMKeyConverter converter = new JcaPEMKeyConverter();
            Object object = pemParser.readObject();

            while (object != null) {
                if (object instanceof X509CertificateHolder) {
                    cert = (X509CertificateHolder) object;
                } else if (object instanceof PEMKeyPair) {
                    PEMKeyPair keyPair = (PEMKeyPair) object;
                    privateKey = converter.getPrivateKey(keyPair.getPrivateKeyInfo());
                } else if (object instanceof PrivateKeyInfo) {
                    privateKey = converter.getPrivateKey((PrivateKeyInfo) object);
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

    // Placeholder for the Certificate class
    public static class Certificate {
        private byte[] certificateData;
        private byte[] privateKeyData;
        private String subject;
        private String issuer;
        private java.util.Date expirationDate;
        private BigInteger serialNumber;

        public byte[] getCertificateData() {
            return certificateData;
        }

        public void setCertificateData(byte[] certificateData) {
            this.certificateData = certificateData;
        }

        public byte[] getPrivateKeyData() {
            return privateKeyData;
        }

        public void setPrivateKeyData(byte[] privateKeyData) {
            this.privateKeyData = privateKeyData;
        }

        public String getSubject() {
            return subject;
        }

        public void setSubject(String subject) {
            this.subject = subject;
        }

        public String getIssuer() {
            return issuer;
        }

        public void setIssuer(String issuer) {
            this.issuer = issuer;
        }

        public Date getExpirationDate() {
            return expirationDate;
        }

        public void setExpirationDate(Date expirationDate) {
            this.expirationDate = expirationDate;
        }

        public BigInteger getSerialNumber() {
            return serialNumber;
        }

        public void setSerialNumber(BigInteger serialNumber) {
            this.serialNumber = serialNumber;
        }

        // Getters, setters, and other methods...
    }
}
