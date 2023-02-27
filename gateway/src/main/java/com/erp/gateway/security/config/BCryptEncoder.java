package com.erp.gateway.security.config;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Component
public class BCryptEncoder extends BCryptPasswordEncoder {

    public String encodeToken(String token) {
        byte[] signature = Base64.getEncoder().encode(token.split("\\.")[2].getBytes(StandardCharsets.UTF_8));
        return super.encode(new String(signature) + token);
    }

    public boolean matchTokens(String token, String encodedToken) {
        byte[] signature = Base64.getEncoder().encode(token.split("\\.")[2].getBytes(StandardCharsets.UTF_8));
        return super.matches(new String(signature) + token, encodedToken);
    }
}