package com.lichbalab.ksc.service;

import java.util.List;

import com.lichbalab.ksc.dto.CertificateDto;
import com.lichbalab.ksc.mapper.CertificateMapper;
import com.lichbalab.ksc.model.Certificate;
import com.lichbalab.ksc.repository.CertificateRepository;
import org.springframework.stereotype.Service;

@Service
public class CertificateService {
    private final CertificateRepository certificateRepository;
    private final CertificateMapper     mapper;

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
}