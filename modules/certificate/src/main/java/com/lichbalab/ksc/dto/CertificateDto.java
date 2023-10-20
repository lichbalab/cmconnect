package com.lichbalab.ksc.dto;

import java.math.BigInteger;
import java.util.Date;

public class CertificateDto {

    private Long       id;
    private String     alias;
    private BigInteger serialNumber;
    private Date       expirationDate;
    private String     subject;
    private String     issuer;
    private byte[]     certificateData;
    private byte[]     privateKeyData;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public BigInteger getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(BigInteger serialNumber) {
        this.serialNumber = serialNumber;
    }

    public Date getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
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
}
