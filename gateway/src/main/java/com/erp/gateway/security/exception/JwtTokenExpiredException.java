package com.erp.gateway.security.exception;


import com.erp.gateway.security.token.JwtToken;
import org.springframework.security.core.AuthenticationException;

import java.io.Serial;

public class JwtTokenExpiredException extends AuthenticationException {
    @Serial
    private static final long serialVersionUID = -5959543783324224864L;

    private JwtToken token;

    public JwtTokenExpiredException(String msg) {
        super(msg);
    }

    public JwtTokenExpiredException(JwtToken token, String msg, Throwable t) {
        super(msg, t);
        this.token = token;
    }

    public String token() {
        return this.token.getToken();
    }
}