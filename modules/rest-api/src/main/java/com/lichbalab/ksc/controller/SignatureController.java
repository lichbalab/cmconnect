package com.lichbalab.ksc.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/signatures")
public class SignatureController {

    @PostMapping("/create")
    public ResponseEntity<MultipartFile> createSignature(@RequestParam("document") MultipartFile document, @RequestParam("alias") String certificateAlias) {
        return null;
    }
}
