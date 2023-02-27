package com.erp.core_client;

import com.erp.core_data.enumeration.UserStatus;
import com.erp.core_data.model.request.ConfirmationRequest;
import com.erp.core_data.model.request.RegistrationRequest;
import com.erp.core_data.model.request.StatusUpdateRequest;
import com.erp.core_data.model.response.RegistrationResponse;
import com.erp.core_data.model.response.UserResponse;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;


@FeignClient("${core.name}")
public interface UserClient {

    @PutMapping(path = "${core.registrationUrl}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    RegistrationResponse register(
            @RequestPart RegistrationRequest request,
            @RequestPart(required = false) MultipartFile photo
    );

    @PostMapping(path = "${core.confirmationUrl}")
    void confirmEmail(@PathVariable Long userId, @RequestBody ConfirmationRequest request);

    @GetMapping(path = "${core.userUrl}")
    Page<UserResponse> getUsers(
            @RequestParam(required = false) String search,
            @RequestParam UserStatus status,
            @Parameter(hidden = true) Pageable pageable
    );

    @PutMapping(path = "${core.userStatusUrl}")
    void updateUserStatus(@PathVariable Long userId, @RequestBody StatusUpdateRequest request);

    @DeleteMapping(path = "${core.userIdUrl}")
    void deleteUser(@PathVariable Long userId, @RequestParam Long updatedBy);
}
