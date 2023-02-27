package com.erp.gateway.security.filter;

import com.erp.auth_data.model.request.AuthRequest;
import com.erp.gateway.security.exception.AuthMethodNotSupportedException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import static com.erp.auth_data.constant.ExceptionConstants.INVALID_CREDENTIALS;

@Slf4j
public class LoginRequestFilter extends AbstractAuthenticationProcessingFilter {

    private final AuthenticationSuccessHandler successHandler;
    private final AuthenticationFailureHandler failureHandler;
    private final ObjectMapper objectMapper;

    public LoginRequestFilter(String defaultProcessUrl, AuthenticationSuccessHandler successHandler,
                              AuthenticationFailureHandler failureHandler, ObjectMapper objectMapper) {
        super(defaultProcessUrl);
        this.successHandler = successHandler;
        this.failureHandler = failureHandler;
        this.objectMapper = objectMapper;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException, IOException {
        if (!HttpMethod.POST.name().equals(request.getMethod())) {
            throw new AuthMethodNotSupportedException("Authentication method not supported");
        }

        AuthRequest authRequest = objectMapper.readValue(request.getReader(), AuthRequest.class);
        validateAuthRequest(authRequest);

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                authRequest.getEmail(),
                authRequest.getPassword()
        );

        return this.getAuthenticationManager().authenticate(authenticationToken);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
                                            Authentication authResult) throws IOException, ServletException {
        log.debug("Authentication Sucessful");
        successHandler.onAuthenticationSuccess(request, response, authResult);
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                              AuthenticationException failed) throws IOException, ServletException {
        log.error("Authentication Failed");
        SecurityContextHolder.clearContext();
        failureHandler.onAuthenticationFailure(request, response, failed);
    }

    private void validateAuthRequest(AuthRequest authRequest) {
        Predicate<AuthRequest> invalidEmail = request ->
                StringUtils.isBlank(request.getEmail());
        Predicate<AuthRequest> invalidPattern = request ->
                !Pattern.compile("^(.+)@(\\S+)$").matcher(request.getEmail()).matches();
        Predicate<AuthRequest> invalidPassword = request ->
                StringUtils.isBlank(request.getPassword());

        Predicate<AuthRequest> invalidRequest = invalidEmail.or(invalidPattern).or(invalidPassword);

        if (invalidRequest.test(authRequest)) {
            throw new AuthenticationServiceException(INVALID_CREDENTIALS);
        }
    }
}
