package com.erp.gateway.security.util;

import com.erp.auth_data.enumeration.TokenType;
import com.erp.core_client.AuthClient;
import com.erp.core_data.enumeration.Role;
import com.erp.core_data.model.AuthTokenModel;
import com.erp.gateway.security.CustomUserDetails;
import com.erp.gateway.security.config.BCryptEncoder;
import com.erp.gateway.security.token.JwtToken;
import com.erp.shared_data.exception.FeignClientException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.EnumMap;
import java.util.Map;
import java.util.function.Function;

import static com.erp.auth_data.constant.AuthConstants.ROLE_ATTRIBUTE;
import static com.erp.auth_data.constant.AuthConstants.TOKEN_TYPE_CLAIM;
import static com.erp.auth_data.constant.ExceptionConstants.INVALID_TOKEN;

@Service
@RequiredArgsConstructor
@Slf4j
public class JwtUtilService {

    private static final Map<TokenType, Function<AuthTokenModel, String>> tokenMap = new EnumMap<>(TokenType.class);

    private final AuthClient authClient;
    private final BCryptEncoder encoder;

    @Value("${security.jwt.secretKey}")
    private String secretKey;
    @Value("${security.jwt.tokenExpirationTime}")
    private int tokenExpirationTime;
    @Value("${security.jwt.refreshTokenExpTime}")
    private int refreshTokenExpTime;

    @PostConstruct
    public void init() {
        tokenMap.put(TokenType.ACCESS_TOKEN, AuthTokenModel::getAccessToken);
        tokenMap.put(TokenType.REFRESH_TOKEN, AuthTokenModel::getRefreshToken);
    }


    public UserDetails buildUserDetails(Authentication authentication, TokenType expectedTokenType) {
        Claims claims = parseToken((String) authentication.getCredentials());
        String token = (String) authentication.getCredentials();

        Long userId = Long.valueOf(claims.getId());
        TokenType tokenType = TokenType.valueOf((String) claims.get(TOKEN_TYPE_CLAIM));
        Role role = Role.valueOf((String) claims.get(ROLE_ATTRIBUTE));

        AuthTokenModel tokenModel;
        try {
            tokenModel = authClient.getToken(userId);
        } catch (FeignClientException e) {
            throw new BadCredentialsException(INVALID_TOKEN);
        }

        if (!encoder.matchTokens(token, tokenMap.get(tokenType).apply(tokenModel))){
            throw new BadCredentialsException(INVALID_TOKEN);
        }

        if (!expectedTokenType.equals(tokenType)) {
            throw new BadCredentialsException(INVALID_TOKEN);
        }

        return new CustomUserDetails(userId, role);
    }

    public JwtToken generateToken(CustomUserDetails userDetails) {
        Claims claims = Jwts.claims()
                .setId(String.valueOf(userDetails.getUserId()));
        claims.put(TOKEN_TYPE_CLAIM, TokenType.ACCESS_TOKEN);
        claims.put(ROLE_ATTRIBUTE, userDetails.getRole());

        String token = createToken(claims);
        return new JwtToken(token);
    }

    public JwtToken generateRefreshToken(CustomUserDetails userDetails) {
        Claims claims = Jwts.claims()
                .setId(String.valueOf(userDetails.getUserId()));
        claims.put(TOKEN_TYPE_CLAIM, TokenType.REFRESH_TOKEN);
        claims.put(ROLE_ATTRIBUTE, userDetails.getRole());

        String token = createRefreshToken(claims);
        return new JwtToken(token);
    }

    private String createToken(Claims claims) {
        Instant expirationDateTime = Instant.now().plus(tokenExpirationTime, ChronoUnit.MINUTES);
        long expirationDateTimeL = expirationDateTime.toEpochMilli();

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(expirationDateTimeL))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    private String createRefreshToken(Claims claims) {
        Instant expirationDateTime = Instant.now().plus(refreshTokenExpTime, ChronoUnit.MINUTES);
        long expirationDateTimeL = expirationDateTime.toEpochMilli();

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(expirationDateTimeL))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    public Claims parseToken(String token) {
        try {
            return Jwts.parser()
                    .setSigningKey(secretKey)
                    .parseClaimsJws(token)
                    .getBody();
        } catch (UnsupportedJwtException | MalformedJwtException | IllegalArgumentException | SignatureException |
                 ExpiredJwtException ex) {
            log.error(INVALID_TOKEN, ex);
            throw new BadCredentialsException(INVALID_TOKEN, ex);
        }
    }
}
