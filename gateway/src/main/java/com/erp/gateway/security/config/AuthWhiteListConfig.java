package com.erp.gateway.security.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AuthWhiteListConfig {

    @Bean
    @Qualifier("authWhiteList")
    public String[] getAuthWhiteList() {
        return new String[]{
                "/v1/auth",
                "/v1/users/registrations",
                "/v1/users/{userId}/confirmations",
                "/v1/auth/refresh-tokens",
                // -- swagger ui
                "/swagger-resources",
                "/documentation/swagger-ui.html",
                "/swagger-resources/**",
                "/swagger-resources/configuration/ui",
                "/swagger-resources/configuration/security",
                "/swagger-ui.html",
                "v3/api-docs/**",
                "/v3/api-docs/**",
                "/swagger-ui/**",
                "/webjars/**"
                // other public endpoints of your API may be appended to this array
        };
    }

}
