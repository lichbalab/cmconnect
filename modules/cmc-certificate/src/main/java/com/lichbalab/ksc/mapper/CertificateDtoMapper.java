package com.lichbalab.ksc.mapper;

import java.io.IOException;

import com.lichbalab.certificate.Certificate;
import com.lichbalab.certificate.CertificateUtils;
import com.lichbalab.ksc.dto.CertificateDto;

public class CertificateDtoMapper {

    public static CertificateDto certificateToDto(Certificate certificate, String alias) {
        CertificateDto certificateDto = new CertificateDto();
        certificateDto.setSerialNumber(certificate.getSerialNumber());
        certificateDto.setAlias(alias);
        certificateDto.setExpirationDate(certificate.getExpirationDate());
        certificateDto.setSubject(certificate.getSubject());
        certificateDto.setIssuer(certificate.getIssuer());
        certificateDto.setCertificateChainData(certificate.getCertificateChainData());
        certificateDto.setPrivateKeyData(certificate.getPrivateKeyData());
        return certificateDto;
    }

    public static Certificate dtoToCertificate(CertificateDto certificateDto) throws IOException {
        Certificate certificate = new Certificate();
        certificate.setSerialNumber(certificateDto.getSerialNumber());
        certificate.setExpirationDate(certificateDto.getExpirationDate());
        certificate.setSubject(certificateDto.getSubject());
        certificate.setIssuer(certificateDto.getIssuer());
        certificate.setCertificateChainData(certificateDto.getCertificateChainData());
        certificate.setPrivateKeyData(certificateDto.getPrivateKeyData());
        certificate.setCertChain(CertificateUtils.byteArrayToCertChain(certificate.getCertificateChainData()));
        return certificate;
    }
}