package com.lichbalab.ksc.core;

public class CmcException extends RuntimeException {

    private final ErrorCode errorCode;

    public CmcException(Throwable cause, ErrorCode errorCode) {
        super(cause);
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}