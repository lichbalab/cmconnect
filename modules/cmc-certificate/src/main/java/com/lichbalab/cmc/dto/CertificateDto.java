package com.lichbalab.cmc.dto;

import java.math.BigInteger;
import java.util.Date;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class CertificateDto {

    private Long       id;
    private String     alias;
    private BigInteger serialNumber;
    private Date       expirationDate;
    private String     subject;
    private String     issuer;
    private byte[]     certificateChainData;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof CertificateDto that)) return false;

        return new EqualsBuilder().append(serialNumber, that.serialNumber).append(expirationDate, that.expirationDate).append(subject, that.subject).append(issuer, that.issuer).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(serialNumber).append(expirationDate).append(subject).append(issuer).toHashCode();
    }
}
