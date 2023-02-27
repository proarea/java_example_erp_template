package com.erp.shared_data.exception;

import lombok.Getter;

@Getter
public enum ExceptionMessageCode {

    BAD_REQUEST_EXCEPTION_MESSAGE("Bad request"),
    UNAUTHORIZED_EXCEPTION_MESSAGE("Unauthorized"),
    FORBIDDEN_EXCEPTION_MESSAGE("Forbidden"),
    INTERNAL_SERVER_ERROR_MESSAGE("Internal server error"),
    NOT_FOUND_EXCEPTION_MESSAGE("Not found"),
    LIQUIBASE_EXCEPTION_MESSAGE("Liquibase execution error"),
    FILE_EXCEPTION_MESSAGE("File task execution error"),
    COMMUNICATION_EXCEPTION_MESSAGE("Communication task execution error");

    private final String message;

    ExceptionMessageCode(String message) {
        this.message = message;
    }
}
