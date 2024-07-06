package com.lichbalab.cmc.spring.sdk;

import com.lichbalab.certificate.Certificate;
import com.lichbalab.certificate.CertificateUtils;
import org.springframework.boot.ssl.SslStoreBundle;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.List;

public class CmcSslStoreBundle implements SslStoreBundle {

    private final KeyStore keyStore;

    private final KeyStore trustStore;

    public CmcSslStoreBundle(List<Certificate> certificates) {
        try {
        this.keyStore = createKeyStore(null);
        this.trustStore = createKeyStore(null);

        for (Certificate certificate : certificates) {
            if (certificate.getPrivateKeyData() != null) {
                addPrivateKey(keyStore,
                        certificate.getPrivateKey(),
                        certificate.getAlias(),
                        certificate.getPrivateKeyPassword(),
                        certificate.getCertChain().stream().map(CertificateUtils::getFromCertHolder).toList());
            }
            else {
                addCertificates(
                        trustStore,
                        certificate.getCertChain().stream().map(CertificateUtils::getFromCertHolder).toList(),
                        certificate.getAlias()
                );
            }
        }
        } catch (Exception ex) {
            throw new IllegalStateException("Unable to create CmcSslStoreBundle: %s".formatted(ex.getMessage()), ex);
        }
    }

    @Override
    public KeyStore getKeyStore() {
        return keyStore;
    }

    @Override
    public String getKeyStorePassword() {
        return null;
    }

    @Override
    public KeyStore getTrustStore() {
        return trustStore;
    }

    private static KeyStore createKeyStore(String type)
            throws KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException {
        KeyStore store = KeyStore.getInstance(StringUtils.hasText(type) ? type : KeyStore.getDefaultType());
        store.load(null);
        return store;
    }

    private static void addPrivateKey(KeyStore keyStore, PrivateKey privateKey, String alias, String keyPassword,
                                      List<X509Certificate> certificateChain) throws KeyStoreException {
        keyStore.setKeyEntry(alias, privateKey, (keyPassword != null) ? keyPassword.toCharArray() : null,
                certificateChain.toArray(X509Certificate[]::new));
    }

    private static void addCertificates(KeyStore keyStore, List<X509Certificate> certificates, String alias)
            throws KeyStoreException {
        for (int index = 0; index < certificates.size(); index++) {
            String entryAlias = alias + ((certificates.size() == 1) ? "" : "-" + index);
            X509Certificate certificate = certificates.get(index);
            keyStore.setCertificateEntry(entryAlias, certificate);
        }
    }
}