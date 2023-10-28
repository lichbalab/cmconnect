package com.lichbalab.ksc.mapper;

import com.lichbalab.certificate.Certificate;
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

    public static Certificate dtoToCertificate(CertificateDto certificateDto) {
        Certificate certificate = new Certificate();
        certificate.setSerialNumber(certificateDto.getSerialNumber());
        certificate.setExpirationDate(certificateDto.getExpirationDate());
        certificate.setSubject(certificateDto.getSubject());
        certificate.setIssuer(certificateDto.getIssuer());
        certificate.setCertificateChainData(certificateDto.getCertificateChainData());
        certificate.setPrivateKeyData(certificateDto.getPrivateKeyData());
        return certificate;
    }
}