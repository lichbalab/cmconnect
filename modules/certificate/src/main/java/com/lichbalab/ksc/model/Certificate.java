package com.lichbalab.ksc.model;

import java.math.BigInteger;
import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.SequenceGenerator;
import org.hibernate.annotations.Type;
import org.hibernate.usertype.UserType;

@Entity
public class Certificate {


    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "certificate_seq_gen")
    @SequenceGenerator(name = "certificate_seq_gen", sequenceName = "certificate_seq", allocationSize = 1)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "alias")
    private String     alias;
    @Column(name = "serial_number", precision = 38)
    private BigInteger serialNumber;

    @Column(name = "expiration_date")
    private Date expirationDate;

    @Column(name = "subject")
    private String subject;
    @Column(name = "issuer")
    private String issuer;

    @Column(name = "certificate_chain_data", columnDefinition = "bytea")
    private byte[] certificateChainData;

    @Column(name = "private_key_data", columnDefinition = "bytea")
    private byte[] privateKeyData;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getAlias() {
        return alias;
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

    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }

    public String getIssuer() {
        return issuer;
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
}