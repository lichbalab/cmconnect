package com.lichbalab.cmc.sdk.client;

import com.lichbalab.certificate.Certificate;
import com.lichbalab.certificate.CertificateUtils;
import com.lichbalab.certificate.dto.CertificateDto;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

class CmcClientImpl implements CmcClient {
    private final WebClient webClient;

    public CmcClientImpl(WebClient webClient) {
        this.webClient = webClient;
    }

    @Override
    public Certificate getCertificateByAlias(String alias) {
        Mono<CertificateDto> mono = webClient.get()
                 .uri("/certificates/{alias}", alias)
                 .retrieve()
                 .bodyToMono(CertificateDto.class);

        CertificateDto cert = mono.block();
        return CertificateUtils.buildFromDto(cert);
    }
}