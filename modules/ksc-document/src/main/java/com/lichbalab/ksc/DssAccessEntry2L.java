package com.lichbalab.ksc;

import java.security.PrivateKey;

import com.lichbalab.certificate.Certificate;
import com.lichbalab.certificate.CertificateUtils;
import eu.europa.esig.dss.enumerations.EncryptionAlgorithm;
import eu.europa.esig.dss.model.x509.CertificateToken;
import eu.europa.esig.dss.token.DSSPrivateKeyAccessEntry;
import org.bouncycastle.openssl.PEMException;

public class DssAccessEntry2L implements DSSPrivateKeyAccessEntry {

    private final PrivateKey privateKey;
    private final CertificateToken certificateToken;
    private final CertificateToken[] certificateChain;
    private final EncryptionAlgorithm encryptionAlgorithm;

    public DssAccessEntry2L(Certificate certificate) throws PEMException {
        certificateToken = SignUtil.getDssCertificateToken(certificate);
        certificateChain = SignUtil.getDssCertificateChain(certificate).toArray(new CertificateToken[0]);
        encryptionAlgorithm = EncryptionAlgorithm.forKey(certificateToken.getPublicKey());
        privateKey = CertificateUtils.convertBytesToPrivateKey(certificate.getPrivateKeyData());
    }

    @Override
    public PrivateKey getPrivateKey() {
        return null;
    }

    @Override
    public CertificateToken getCertificate() {
        return null;
    }

    @Override
    public CertificateToken[] getCertificateChain() {
        return new CertificateToken[0];
    }

    @Override
    public EncryptionAlgorithm getEncryptionAlgorithm() {
        return null;
    }
}