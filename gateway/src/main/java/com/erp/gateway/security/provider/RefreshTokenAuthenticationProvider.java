package com.erp.gateway.security.provider;

import com.erp.auth_data.enumeration.TokenType;
import com.erp.gateway.security.token.RefreshTokenAuthenticationToken;
import com.erp.gateway.security.util.JwtUtilService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RefreshTokenAuthenticationProvider implements AuthenticationProvider {

    private final JwtUtilService jwtUtilService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        log.debug("Attempting to parse token");
        UserDetails userDetails = jwtUtilService.buildUserDetails(authentication, TokenType.REFRESH_TOKEN);
        return new RefreshTokenAuthenticationToken(userDetails, userDetails.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(RefreshTokenAuthenticationToken.class);
    }

}
