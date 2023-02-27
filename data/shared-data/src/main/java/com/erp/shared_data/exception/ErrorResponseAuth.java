package com.erp.shared_data.exception;

import org.springframework.http.HttpStatus;

import java.util.Date;

public class ErrorResponseAuth {
    // HTTP TenantCoreResponse Status Code
    private final HttpStatus status;

    // General Error message
    private final String message;

    // Error code
    private final ErrorCode errorCode;

    private final Date timestamp;

    protected ErrorResponseAuth(final String message, final ErrorCode errorCode, HttpStatus status) {
        this.message = message;
        this.errorCode = errorCode;
        this.status = status;
        this.timestamp = new Date();
    }

    public static ErrorResponseAuth of(final String message, final ErrorCode errorCode, HttpStatus status) {
        return new ErrorResponseAuth(message, errorCode, status);
    }

    public Integer getStatus() {
        return status.value();
    }

    public String getMessage() {
        return message;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public Date getTimestamp() {
        return timestamp;
    }

}
