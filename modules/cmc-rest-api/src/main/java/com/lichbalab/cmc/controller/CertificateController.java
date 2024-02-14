package com.lichbalab.cmc.controller;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import com.lichbalab.certificate.Certificate;
import com.lichbalab.certificate.CertificateUtils;
import com.lichbalab.cmc.dto.CertificateDto;
import com.lichbalab.cmc.mapper.CertificateDtoMapper;
import com.lichbalab.cmc.service.CertificateService;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/certificates")
public class CertificateController {

    private final CertificateService certificateService;

    @Autowired
    public CertificateController(CertificateService certificateService) {
        this.certificateService = certificateService;
    }

    @PostMapping
    public ResponseEntity<CertificateDto> createCertificate(@RequestBody CertificateDto certificate) {
        return new ResponseEntity<>(certificateService.createCertificate(certificate), HttpStatus.CREATED);
    }

    @PostMapping("/upload")
    public ResponseEntity<CertificateDto> createCertificateFromFile(@RequestParam("file") MultipartFile file, @RequestParam("alias") String alias) throws IOException {
        // Convert the file content to a CertificateDto (Placeholder, will need more details on this conversion)
        Certificate certificate = CertificateUtils.buildFromPEM(new InputStreamReader(file.getInputStream()));
        CertificateDto certificateDto = CertificateDtoMapper.certificateToDto(certificate, alias);

        // Use the certificateService to create the certificate
        CertificateDto createdCertificate = certificateService.createCertificate(certificateDto);

        // Return the created certificate as a response
        return new ResponseEntity<>(createdCertificate, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<CertificateDto>> getAllCertificates() {
        return new ResponseEntity<>(certificateService.getAllCertificates(), HttpStatus.OK);
    }

    @GetMapping("/{certificateId}")
    public ResponseEntity<CertificateDto> getCertificateById(@PathVariable Long certificateId) {
        CertificateDto certificate = certificateService.getCertificateById(certificateId);
        if (certificate != null) {
            return new ResponseEntity<>(certificate, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PutMapping("/{certificateId}")
    public ResponseEntity<CertificateDto> updateCertificate(@PathVariable Long certificateId, @RequestBody CertificateDto certificate) {
        CertificateDto updatedCertificate = certificateService.updateCertificate(certificateId, certificate);
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
