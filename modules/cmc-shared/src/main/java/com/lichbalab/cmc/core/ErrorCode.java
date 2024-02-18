package com.lichbalab.cmc.core;

public enum ErrorCode {
    GENERAL(500),
    CERTIFICATE_NOT_FOUND(404);
    final int status;

    ErrorCode(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }
}