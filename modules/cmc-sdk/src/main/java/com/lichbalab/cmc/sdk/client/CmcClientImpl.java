package com.lichbalab.cmc.sdk.client;

import com.lichbalab.certificate.Certificate;
import com.lichbalab.certificate.CertificateUtils;
import com.lichbalab.certificate.dto.CertificateDto;
import com.lichbalab.cmc.core.exception.CmcRuntimeException;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import reactor.core.publisher.Flux;

import java.util.Collections;
import java.util.List;

class CmcClientImpl implements CmcClient {
    private final WebClient webClient;

    public CmcClientImpl(WebClient webClient) {
        this.webClient = webClient;
    }

    @Override
    public Certificate getCertificateByAlias(String alias) {
        Flux<CertificateDto> flux = webClient.get()
                .uri("/certificates/alias/{alias}", alias)
                .retrieve()
                .bodyToFlux(CertificateDto.class);
        flux = handleErrors(flux);

        List<CertificateDto> certDtoList = flux.collectList().block();
        if (certDtoList == null || certDtoList.isEmpty()) {
            return null;
        }
        return CertificateUtils.buildFromDto(certDtoList.get(0));
    }

    @Override
    public List<Certificate> getCertificates() {
        Flux<CertificateDto> flux = webClient.get()
                .uri("/certificates")
                .retrieve()
                .bodyToFlux(CertificateDto.class);
        flux = handleErrors(flux);

        List<CertificateDto> certDtoList = flux.collectList().block();

        if (certDtoList == null) {
            return Collections.emptyList();
        }

        return certDtoList.stream()
                .map(CertificateUtils::buildFromDto)
                .toList();
    }

    private <T> Flux<T> handleErrors(Flux<T> flux) {
        return flux.onErrorMap(WebClientRequestException.class, ex -> new CmcClientException("Error calling cmc-rest-api", ex))
                .onErrorMap(CmcRuntimeException.class, ex -> {throw ex;})
                .doOnError(CmcRuntimeException.class, ex -> {throw ex;});
    }
}