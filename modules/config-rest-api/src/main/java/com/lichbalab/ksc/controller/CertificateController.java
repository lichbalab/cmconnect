package com.lichbalab.ksc.controller;

import java.util.List;

import com.lichbalab.ksc.model.Certificate;
import com.lichbalab.ksc.service.CertificateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/certificates")
public class CertificateController {

    @Autowired
    private CertificateService certificateService;

    @PostMapping
    public ResponseEntity<Certificate> createCertificate(@RequestBody Certificate certificate) {
        return new ResponseEntity<>(certificateService.createCertificate(certificate), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Certificate>> getAllCertificates() {
        return new ResponseEntity<>(certificateService.getAllCertificates(), HttpStatus.OK);
    }

    @GetMapping("/{certificateId}")
    public ResponseEntity<Certificate> getCertificateById(@PathVariable Long certificateId) {
        Certificate certificate = certificateService.getCertificateById(certificateId);
        if (certificate != null) {
            return new ResponseEntity<>(certificate, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PutMapping("/{certificateId}")
    public ResponseEntity<Certificate> updateCertificate(@PathVariable Long certificateId, @RequestBody Certificate certificate) {
        Certificate updatedCertificate = certificateService.updateCertificate(certificateId, certificate);
        if (updatedCertificate != null) {
            return new ResponseEntity<>(updatedCertificate, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/{certificateId}")
    public ResponseEntity<Void> deleteCertificate(@PathVariable Long certificateId) {
        certificateService.deleteCertificate(certificateId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
