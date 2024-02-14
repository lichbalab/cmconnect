package com.lichbalab.cmc.controller;

import com.lichbalab.cmc.doc.DocSignService;
import eu.europa.esig.dss.model.DSSDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

@RestController
@RequestMapping("/docs")
public class DocController {
    private final DocSignService docSignService;

    @Autowired
    public DocController(DocSignService docSignService) {
        this.docSignService = docSignService;
    }

    @PostMapping("/sign/pdf")
    public ResponseEntity<StreamingResponseBody> signPdf(@RequestParam("document") MultipartFile document, @RequestParam("alias") String certificateAlias) {

        StreamingResponseBody responseBody = outputStream -> {
            DSSDocument signedDoc = docSignService.signPdf(document.getInputStream(), certificateAlias);
            signedDoc.writeTo(outputStream);
            // Write data to the outputStream
            // This could be streaming file data, generating data on the fly, etc.
        };
        return ResponseEntity.ok()
                 .contentType(MediaType.APPLICATION_PDF)
                 .body(responseBody);
    }
}