package com.erp.gateway.security.provider;

import com.erp.core_client.AuthClient;
import com.erp.core_data.model.response.UserDetailsResponse;
import com.erp.gateway.security.CustomUserDetails;
import com.erp.shared_data.exception.FeignClientException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import static com.erp.auth_data.constant.ExceptionConstants.INVALID_CREDENTIALS;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoginAuthenticationProvider implements AuthenticationProvider {

    private final AuthClient authClient;
    private final BCryptPasswordEncoder encoder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        log.debug("Attempting to verify user credentials");

        Assert.notNull(authentication, "No authentication data provided");

        UsernamePasswordAuthenticationToken authenticationToken = (UsernamePasswordAuthenticationToken) authentication;

        String email = (String) authenticationToken.getPrincipal();
        String password = (String) authenticationToken.getCredentials();

        UserDetailsResponse response;
        try {
            response = authClient.getUserDetails(email);
        } catch (FeignClientException e) {
            throw new AuthenticationServiceException(INVALID_CREDENTIALS);
        }
        validateUser(password, response);

        CustomUserDetails userDetails = new CustomUserDetails(response);

        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                response.getUserId(),
                null,
                null
        );

        token.setDetails(userDetails);

        return token;
    }

    public void validateUser(String password, UserDetailsResponse user) {
        if (!encoder.matches(password, user.getPassword())) {
            throw new AuthenticationServiceException(INVALID_CREDENTIALS);
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}
