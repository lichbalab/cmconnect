package com.lichbalab.cmc.model;

import java.math.BigInteger;
import java.util.Date;

import jakarta.persistence.*;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"alias"}))
public class Certificate {


    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "certificate_seq_gen")
    @SequenceGenerator(name = "certificate_seq_gen", sequenceName = "certificate_seq", allocationSize = 1)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "alias")
    private String     alias;
    @Column(name = "serial_number")
    private String serialNumber;

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

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof Certificate that)) return false;

        return new EqualsBuilder().append(alias, that.alias).append(serialNumber, that.serialNumber).append(subject, that.subject).append(issuer, that.issuer).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(alias).append(serialNumber).append(subject).append(issuer).toHashCode();
    }
}
