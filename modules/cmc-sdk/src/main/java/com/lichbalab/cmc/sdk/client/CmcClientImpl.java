package com.lichbalab.cmc.sdk.client;

import com.lichbalab.certificate.Certificate;
import com.lichbalab.certificate.CertificateUtils;
import com.lichbalab.certificate.dto.CertificateDto;
import com.lichbalab.cmc.core.exception.CmcRuntimeException;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import reactor.core.publisher.Mono;

class CmcClientImpl implements CmcClient {
    private final WebClient webClient;

    public CmcClientImpl(WebClient webClient) {
        this.webClient = webClient;
    }

    @Override
    public Certificate getCertificateByAlias(String alias) {
        Mono<CertificateDto> mono = webClient.get()
                 .uri("/certificates/alias/{alias}", alias)
                 .retrieve()
                 .bodyToMono(CertificateDto.class)
                 .onErrorMap(WebClientRequestException.class, ex -> new CmcClientException("Error calling cmc-rest-api", ex))
                 .onErrorMap(CmcRuntimeException.class, ex -> {throw ex;})
                 .doOnError(CmcRuntimeException.class, ex -> {throw ex;})
        ;

        CertificateDto cert = mono.block();
        return CertificateUtils.buildFromDto(cert);
    }
}