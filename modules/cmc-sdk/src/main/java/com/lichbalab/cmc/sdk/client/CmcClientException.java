package com.lichbalab.cmc.sdk.client;

public class CmcClientException extends RuntimeException {

    public CmcClientException(String message) {
        super(message);
    }

    public CmcClientException(String message, Throwable cause) {
        super(message, cause);
    }

    public CmcClientException(Throwable cause) {
        super(cause);
    }
}
