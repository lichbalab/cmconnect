package com.lichbalab.cmc.sdk.client;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import reactor.core.publisher.Mono;

public class ErrorHandler {

    public static ExchangeFilterFunction handleError() {
        return ExchangeFilterFunction.ofResponseProcessor(clientResponse -> {
            if (clientResponse.statusCode().isError()) {
                return parseJsonError(clientResponse);
            }
            return Mono.just(clientResponse);
        });
    }

    public static Mono<ClientResponse> parseJsonError(ClientResponse clientResponse) {
        return clientResponse.bodyToMono(JsonNode.class)
                 .switchIfEmpty(Mono.error(new CmcClientException("Failed to get certificate. Cause: Response body is empty")))
                 .flatMap(jsonNode ->
                          Mono.error(new CmcClientException("Failed to get certificate. Cause: " + jsonNode.get("message").asText()))
                 );
    }
}