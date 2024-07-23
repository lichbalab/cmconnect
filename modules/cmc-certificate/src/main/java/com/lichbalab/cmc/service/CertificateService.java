package com.lichbalab.cmc.service;

import java.util.List;

import com.lichbalab.certificate.dto.CertificateDto;
import com.lichbalab.cmc.mapper.CertificateMapper;
import com.lichbalab.cmc.model.Certificate;
import com.lichbalab.cmc.repository.CertificateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CertificateService {
    private final CertificateRepository certificateRepository;
    private final CertificateMapper     mapper;

    @Autowired
    public CertificateService(CertificateRepository certificateRepository, CertificateMapper mapper) {
        this.certificateRepository = certificateRepository;
        this.mapper = mapper;
    }

    public CertificateDto createCertificate(CertificateDto certificate) {
        Certificate dom = mapper.dtoToDom(certificate);
        return mapper.domToDto(certificateRepository.save(dom));
    }

    public List<CertificateDto> getAllCertificates() {
        return certificateRepository.findAll().stream().map(mapper::domToDto).toList();
    }

    public CertificateDto getCertificateById(Long id) {
        return mapper.domToDto(certificateRepository.findById(id).orElse(null));
    }

    public CertificateDto updateCertificate(Long id, CertificateDto certificate) {
        if (certificateRepository.existsById(id)) {
            certificate.setId(id);
            Certificate dom = mapper.dtoToDom(certificate);
            return mapper.domToDto(certificateRepository.save(dom));
        }
        return null;
    }

    public void deleteCertificate(Long id) {
        certificateRepository.deleteById(id);
    }

    public CertificateDto getCertByAlias(String alias) {
        return mapper.domToDto(certificateRepository.getCertByAlias(alias));
    }

    public void deleteCertificateByAlias(String alias) {
        Certificate cert = certificateRepository.getCertByAlias(alias);
        if (cert != null) {
            certificateRepository.delete(cert);
        }
    }
}