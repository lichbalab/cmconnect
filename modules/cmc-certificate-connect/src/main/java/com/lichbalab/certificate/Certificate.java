package com.lichbalab.certificate;

import java.math.BigInteger;
import java.security.PrivateKey;
import java.util.Date;
import java.util.List;

import org.bouncycastle.cert.X509CertificateHolder;

public class Certificate {
    private List<X509CertificateHolder> certChain;
    private byte[]                      certificateChainData;
    private byte[]                      privateKeyData;
    private String                      subject;
    private String                      issuer;
    private Date                        expirationDate;
    private String serialNumber;
    private String alias;
    
    // Add a static Set to keep track of used aliases
    private static final Set<String> usedAliases = new HashSet<>();
    private String privateKeyPassword;
    private PrivateKey privateKey;

    public byte[] getCertificateChainData() {
        return certificateChainData;
    }

    public void setCertificateChainData(byte[] certificateChainData) {
        this.certificateChainData = certificateChainData;
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

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public List<X509CertificateHolder> getCertChain() {
        return certChain;
    }

    public void setCertChain(List<X509CertificateHolder> certChain) {
        this.certChain = certChain;
    }

    public void setAlias(String alias) {
        if (usedAliases.contains(alias)) {
            throw new IllegalArgumentException("Alias must be unique. The alias '" + alias + "' is already in use.");
        }
        if (this.alias != null) {
            usedAliases.remove(this.alias);
        }
        this.alias = alias;
        usedAliases.add(alias);
    }

    public String getAlias() {
        return alias;
    }

    public void setPrivateKeyPassword(String privateKeyPassword) {
        this.privateKeyPassword = privateKeyPassword;
    }

    public String getPrivateKeyPassword() {
        return privateKeyPassword;
    }

    public void setPrivateKey(PrivateKey privateKey) {
        this.privateKey = privateKey;
    }

    public PrivateKey getPrivateKey() {
        return privateKey;
    }
}
