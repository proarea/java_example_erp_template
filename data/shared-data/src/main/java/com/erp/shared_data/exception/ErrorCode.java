package com.erp.shared_data.exception;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ErrorCode {
    GLOBAL(2),
    PROCESSING_ERROR(5),
    APPLICATION_ERROR(6),
    AUTHENTICATION(10),
    JWT_TOKEN_EXPIRED(11);

    private final int errorCode;

    ErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    @JsonValue
    public int getErrorCode() {
        return errorCode;
    }

}
