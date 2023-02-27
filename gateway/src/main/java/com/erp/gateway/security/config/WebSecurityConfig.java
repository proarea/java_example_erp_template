package com.erp.gateway.security.config;


import com.erp.gateway.security.filter.JwtRequestFilter;
import com.erp.gateway.security.filter.LoginRequestFilter;
import com.erp.gateway.security.filter.RefreshTokenRequestFilter;
import com.erp.gateway.security.handler.AuthSuccessHandler;
import com.erp.gateway.security.provider.JwtAuthenticationProvider;
import com.erp.gateway.security.provider.LoginAuthenticationProvider;
import com.erp.gateway.security.provider.RefreshTokenAuthenticationProvider;
import com.erp.gateway.security.util.SkipPathRequestMatcher;
import com.erp.gateway.security.util.TokenExtractor;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.springframework.security.config.Customizer.withDefaults;

@Slf4j
@Configuration
@EnableWebSecurity
@ComponentScan("com.erp.*")
@EnableAspectJAutoProxy
@EnableMethodSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {

    private static final String LOGIN_ENTRY_POINT = "/**/auth";
    private static final String REFRESH_TOKEN_ENTRY_POINT = "/**/auth/refresh-tokens";
    private static final String TOKEN_AUTH_ENTRY_POINT = "/**";

    private final RefreshTokenAuthenticationProvider refreshTokenAuthenticationProvider;
    private final LoginAuthenticationProvider loginAuthenticationProvider;
    private final JwtAuthenticationProvider jwtAuthenticationProvider;
    private final AuthenticationFailureHandler failureHandler;
    private final AuthSuccessHandler successHandler;
    private final TokenExtractor tokenExtractor;
    private final ObjectMapper objectMapper;
    private final String[] authWhiteList;

    @Bean
    public ProviderManager authenticationManager() {
        return new ProviderManager(
                Arrays.asList(
                        loginAuthenticationProvider,
                        jwtAuthenticationProvider,
                        refreshTokenAuthenticationProvider
                ));
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(authWhiteList).permitAll()
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .httpBasic(withDefaults())
                .addFilterBefore(buildJwtRequestFilter(), UsernamePasswordAuthenticationFilter.class)
                .addFilterAfter(buildLoginProcessingFilter(), UsernamePasswordAuthenticationFilter.class)
                .addFilterAfter(buildRefreshTokenRequestFilter(), UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Collections.singletonList("*"));
        configuration.addAllowedHeader("*");
        configuration.addAllowedMethod("*");
        configuration.addExposedHeader("Location");
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
    
    private JwtRequestFilter buildJwtRequestFilter() {
        List<String> pathsToSkip = Arrays.asList(authWhiteList);
        SkipPathRequestMatcher matcher = new SkipPathRequestMatcher(pathsToSkip, TOKEN_AUTH_ENTRY_POINT);

        JwtRequestFilter filter = new JwtRequestFilter(
                failureHandler,
                tokenExtractor,
                matcher
        );
        filter.setAuthenticationManager(authenticationManager());
        return filter;
    }

    private RefreshTokenRequestFilter buildRefreshTokenRequestFilter() {
        RefreshTokenRequestFilter refreshTokenRequestFilter = new RefreshTokenRequestFilter(
                REFRESH_TOKEN_ENTRY_POINT,
                successHandler,
                failureHandler,
                objectMapper
        );
        refreshTokenRequestFilter.setAuthenticationManager(authenticationManager());
        return refreshTokenRequestFilter;
    }

    private LoginRequestFilter buildLoginProcessingFilter() {
        LoginRequestFilter loginRequestFilter = new LoginRequestFilter(
                LOGIN_ENTRY_POINT,
                successHandler,
                failureHandler,
                objectMapper
        );
        loginRequestFilter.setAuthenticationManager(authenticationManager());
        return loginRequestFilter;
    }
}
