package com.erp.gateway.service;


import com.erp.core_client.UserClient;
import com.erp.core_data.enumeration.UserStatus;
import com.erp.core_data.model.request.ConfirmationRequest;
import com.erp.core_data.model.request.RegistrationRequest;
import com.erp.core_data.model.request.StatusUpdateRequest;
import com.erp.core_data.model.response.RegistrationResponse;
import com.erp.core_data.model.response.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserClient userClient;

    public RegistrationResponse register(RegistrationRequest request, MultipartFile photo) {
        return userClient.register(request, photo);
    }

    public void confirmEmail(Long userId, ConfirmationRequest request) {
        userClient.confirmEmail(userId, request);
    }

    public Page<UserResponse> getUsers(String search, UserStatus status, Pageable pageable) {
        return userClient.getUsers(search, status, pageable);
    }

    public void updateUserStatus(StatusUpdateRequest request, Long userId, Long updatedBy) {
        request.setUpdatedBy(updatedBy);
        userClient.updateUserStatus(userId, request);
    }

    public void deleteUser(Long userId) {
        deleteUser(userId, userId);
    }

    public void deleteUser(Long userId, Long updatedBy) {
        userClient.deleteUser(userId, updatedBy);
    }
}
