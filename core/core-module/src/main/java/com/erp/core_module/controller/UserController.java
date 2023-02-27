package com.erp.core_module.controller;

import com.erp.core_data.enumeration.UserStatus;
import com.erp.core_data.model.AuthTokenModel;
import com.erp.core_data.model.request.ConfirmationRequest;
import com.erp.core_data.model.request.RegistrationRequest;
import com.erp.core_data.model.request.StatusUpdateRequest;
import com.erp.core_data.model.response.RegistrationResponse;
import com.erp.core_data.model.response.UserDetailsResponse;
import com.erp.core_data.model.response.UserResponse;
import com.erp.core_module.service.TokenService;
import com.erp.core_module.service.UserService;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/users")
public class UserController {

    private final TokenService tokenService;
    private final UserService userService;

    @PutMapping(path = "/registrations", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public RegistrationResponse registerUser(
            @RequestPart RegistrationRequest request,
            @RequestPart(required = false) MultipartFile photo
    ) {
        return userService.registerUser(request, photo);
    }

    @PostMapping("/{userId}/confirmations")
    public ResponseEntity<Void> confirmEmail(
            @PathVariable Long userId,
            @RequestBody ConfirmationRequest request
    ) {
        userService.confirmEmail(request, userId);
        return ResponseEntity.noContent()
                .build();
    }

    @GetMapping("/user-details")
    public UserDetailsResponse getUserDetails(@RequestParam String email) {
        return userService.getUserDetails(email);
    }

    @PutMapping("/{userId}/tokens")
    public ResponseEntity<Void> addToken(
            @PathVariable Long userId,
            @RequestBody AuthTokenModel request
    ) {
        tokenService.addToken(request, userId);
        return ResponseEntity.noContent()
                .build();
    }

    @GetMapping("/{userId}/tokens")
    public AuthTokenModel getToken(@PathVariable Long userId) {
        return tokenService.getToken(userId);
    }

    @GetMapping
    public Page<UserResponse> getUsers(
            @RequestParam(required = false) String search,
            @RequestParam UserStatus status,
            @Parameter(hidden = true) Pageable pageable
    ) {
        return userService.getUsers(search, status, pageable);
    }

    @PutMapping("/{userId}/statuses")
    public ResponseEntity<Void> updateUserStatus(
            @PathVariable Long userId,
            @RequestBody StatusUpdateRequest request
    ) {
        userService.updateUserStatus(request, userId);
        return ResponseEntity.noContent()
                .build();
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(
            @PathVariable Long userId,
            @RequestParam Long updatedBy
    ) {
        userService.deleteUser(userId, updatedBy);
        return ResponseEntity.noContent()
                .build();
    }
}
