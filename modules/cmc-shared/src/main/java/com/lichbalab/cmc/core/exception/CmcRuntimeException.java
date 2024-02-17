package com.lichbalab.cmc.core.exception;

public class CmcRuntimeException extends RuntimeException {

    public CmcRuntimeException() {
        super();
    }

    public CmcRuntimeException(String message) {
        super(message);
    }

    public CmcRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }
}
