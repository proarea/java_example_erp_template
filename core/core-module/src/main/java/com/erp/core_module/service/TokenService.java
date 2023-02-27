package com.erp.core_module.service;

import com.erp.core_data.model.AuthTokenModel;
import com.erp.core_module.entity.TokenEntity;
import com.erp.core_module.repository.TokenRepository;
import com.erp.shared_data.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import static com.erp.shared_data.constant.ExceptionConstants.TOKEN_NOT_EXISTS;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final TokenRepository tokenRepository;
    private final UserService userService;
    private final ModelMapper modelMapper;

    public void addToken(AuthTokenModel request, Long userId) {
        userService.validateUserExistence(userId);

        TokenEntity token = tokenRepository.findByUserId(userId)
                .orElse(new TokenEntity())
                .setAccessToken(request.getAccessToken())
                .setRefreshToken(request.getRefreshToken())
                .setUserId(userId);

        tokenRepository.save(token);
    }

    public AuthTokenModel getToken(Long userId) {
        userService.validateUserExistence(userId);

        TokenEntity token = tokenRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException(String.format(TOKEN_NOT_EXISTS, userId)));

        return modelMapper.map(token, AuthTokenModel.class);
    }
}
