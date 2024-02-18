package com.lichbalab.cmc.core;

public class CmcException extends RuntimeException {

    private final ErrorCode errorCode;
    private final Object[] params;

    public CmcException(Throwable cause, ErrorCode errorCode) {
        super(cause);
        this.errorCode = errorCode;
        params = new Object[]{};
    }

    public CmcException(ErrorCode errorCode, Object[] params) {
        this.errorCode = errorCode;
        this.params = params;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public Object[] getParams() {
        return params;
    }
}