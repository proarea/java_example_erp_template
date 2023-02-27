package com.erp.gateway.security.exception;

import org.springframework.security.authentication.AuthenticationServiceException;

import java.io.Serial;

public class AuthMethodNotSupportedException extends AuthenticationServiceException {

    @Serial
    private static final long serialVersionUID = 3705043083010304496L;

    public AuthMethodNotSupportedException(String msg) {
        super(msg);
    }

}
