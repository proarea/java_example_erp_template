package com.erp.core_module.service;

import com.erp.core_data.enumeration.MediaType;
import com.erp.core_data.enumeration.Role;
import com.erp.core_data.enumeration.UserStatus;
import com.erp.core_data.model.request.ConfirmationRequest;
import com.erp.core_data.model.request.RegistrationRequest;
import com.erp.core_data.model.request.StatusUpdateRequest;
import com.erp.core_data.model.response.RegistrationResponse;
import com.erp.core_data.model.response.UserDetailsResponse;
import com.erp.core_data.model.response.UserResponse;
import com.erp.core_module.entity.UserEntity;
import com.erp.core_module.repository.TokenRepository;
import com.erp.core_module.repository.UserRepository;
import com.erp.shared.util.CodeGeneratorUtil;
import com.erp.shared.util.ValidationUtil;
import com.erp.shared_data.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.erp.shared_data.constant.ExceptionConstants.EMAIL_EXISTS;
import static com.erp.shared_data.constant.ExceptionConstants.EMAIL_NOT_EXISTS;
import static com.erp.shared_data.constant.ExceptionConstants.INVALID_USER_STATUS;
import static com.erp.shared_data.constant.ExceptionConstants.PHONE_EXISTS;
import static com.erp.shared_data.constant.ExceptionConstants.USER_NOT_EXISTS;


@Service
@RequiredArgsConstructor
public class UserService {

    private final CommunicationService communicationService;
    private final ConfirmationService confirmationService;
    private final TokenRepository tokenRepository;
    private final BCryptPasswordEncoder encoder;
    private final UserRepository userRepository;
    private final MediaService mediaService;
    private final ModelMapper modelMapper;

    @Transactional
    public RegistrationResponse registerUser(RegistrationRequest request, MultipartFile photo) {

        UserEntity user = userRepository.findByEmailIgnoreCaseAndStatus(
                request.getEmail(),
                UserStatus.EMAIL_CONFIRMATION
        ).orElse(new UserEntity());

        validateUserExistence(request.getEmail(), request.getPhone(), user.getId());

        modelMapper.map(request, user);

        user = userRepository.save(user
                .setPassword(encoder.encode(request.getPassword()))
                .setStatus(UserStatus.EMAIL_CONFIRMATION));

        String code = CodeGeneratorUtil.generateCode();
        confirmationService.addConfirmation(user.getId(), code);

        String photoUrl = mediaService.uploadPhoto(photo, MediaType.PHOTO, user.getId());
        user.setCreatedBy(user.getId());
        user.setUpdatedBy(user.getId());
        user.setPhotoUrl(photoUrl);

        communicationService.sendCodeEmail(user.getEmail(), code);

        return modelMapper.map(user, RegistrationResponse.class);
    }

    @Transactional
    public void confirmEmail(ConfirmationRequest request, Long userId) {
        UserEntity user = getUserById(userId);

        ValidationUtil.validateOrBadRequest(
                Objects.equals(user.getStatus(), UserStatus.EMAIL_CONFIRMATION),
                String.format(INVALID_USER_STATUS, user.getId(), UserStatus.EMAIL_CONFIRMATION)
        );

        confirmationService.validateCode(user.getId(), request.getCode());

        user.setStatus(UserStatus.WAITING_FOR_APPROVING);

        List<String> emails = userRepository.findAllByRole(Role.ROLE_ADMIN).stream()
                .map(UserEntity::getEmail)
                .collect(Collectors.toList());
        emails.add(user.getEmail());

        communicationService.sendWaitingApprovalEmail(user, emails);
    }

    public UserDetailsResponse getUserDetails(String email) {
        UserEntity user = userRepository.findByEmailIgnoreCaseAndStatus(email, UserStatus.ACTIVE)
                .orElseThrow(() -> new NotFoundException(String.format(EMAIL_NOT_EXISTS, email)));

        return modelMapper.map(user, UserDetailsResponse.class);
    }

    public void validateUserExistence(Long userId) {
        ValidationUtil.validateOrNotFound(
                userRepository.existsById(userId),
                String.format(USER_NOT_EXISTS, userId)
        );
    }

    public Page<UserResponse> getUsers(String search, UserStatus status, Pageable pageable) {
        Page<UserEntity> userEntities = userRepository.searchUsers(search, status.name(), pageable);

        List<UserResponse> userResponses = userEntities.stream()
                .map(user -> modelMapper.map(user, UserResponse.class))
                .toList();

        return new PageImpl<>(userResponses, pageable, userEntities.getTotalElements());
    }

    @Transactional
    public void updateUserStatus(StatusUpdateRequest request, Long userId) {
        validateUserExistence(request.getUpdatedBy());
        UserEntity user = getUserById(userId);

        ValidationUtil.validateOrBadRequest(
                Objects.equals(user.getStatus(), UserStatus.WAITING_FOR_APPROVING),
                String.format(INVALID_USER_STATUS, user.getId(), UserStatus.WAITING_FOR_APPROVING)
        );

        if (request.getApproved()) {
            user.setStatus(UserStatus.ACTIVE);
            user.setRole(request.getRole());
            communicationService.sendApproveEmail(user.getEmail());
        } else {
            user.setStatus(UserStatus.DECLINED);
            communicationService.sendDeclineEmail(user.getEmail());
        }

        user.setUpdatedBy(request.getUpdatedBy());
    }

    @Transactional
    public void deleteUser(Long userId, Long updatedBy) {
        validateUserExistence(updatedBy);
        UserEntity user = getUserById(userId);
        user.delete(updatedBy);

        tokenRepository.deleteAllByUserId(userId);
        communicationService.sendDeletionEmail(user.getEmail());
    }

    private void validateUserExistence(String email, String phone, Long userId) {
        userRepository.findByEmailIgnoreCaseOrPhoneIgnoreCase(email, phone).stream()
                .filter(user -> !Objects.equals(userId, user.getId()))
                .forEach(user -> {
                    ValidationUtil.validateOrBadRequest(
                            !Objects.equals(email, user.getEmail()),
                            String.format(EMAIL_EXISTS, email)
                    );

                    ValidationUtil.validateOrBadRequest(
                            !Objects.equals(phone, user.getPhone()),
                            String.format(PHONE_EXISTS, phone)
                    );
                });
    }

    private UserEntity getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format(USER_NOT_EXISTS, userId)));
    }
}
