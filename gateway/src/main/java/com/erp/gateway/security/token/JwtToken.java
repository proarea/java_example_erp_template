package com.erp.gateway.security.token;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;

@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class JwtToken implements Serializable {
    private final String token;

    public String getToken() {
        return token;
    }

}
