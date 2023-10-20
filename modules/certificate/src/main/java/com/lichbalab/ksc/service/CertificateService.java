package com.lichbalab.ksc.service;

import java.util.List;

import com.lichbalab.ksc.model.Certificate;
import com.lichbalab.ksc.repository.CertificateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CertificateService {

    @Autowired
    private CertificateRepository certificateRepository;

    public Certificate createCertificate(Certificate certificate) {
        return certificateRepository.save(certificate);
    }

    public List<Certificate> getAllCertificates() {
        return certificateRepository.findAll();
    }

    public Certificate getCertificateById(Long id) {
        return certificateRepository.findById(id).orElse(null);
    }

    public Certificate updateCertificate(Long id, Certificate certificate) {
        if (certificateRepository.existsById(id)) {
            certificate.setId(id);
            return certificateRepository.save(certificate);
        }
        return null;
    }

    public void deleteCertificate(Long id) {
        certificateRepository.deleteById(id);
    }
}
