package com.erp.gateway.controller;

import com.erp.core_data.enumeration.UserStatus;
import com.erp.core_data.model.request.StatusUpdateRequest;
import com.erp.core_data.model.response.UserResponse;
import com.erp.gateway.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static com.erp.shared_data.constant.SharedConstants.USER_ID_ATTRIBUTE;

@Tag(name = "Admin API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/admins/users")
@SecurityRequirement(name = "erp")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final UserService userService;

    @GetMapping
    @Parameter(
            in = ParameterIn.QUERY, name = "page",
            schema = @Schema(type = "integer", defaultValue = "0")
    )
    @Parameter(
            in = ParameterIn.QUERY, name = "size",
            schema = @Schema(type = "integer", defaultValue = "20")
    )
    @Parameter(in = ParameterIn.QUERY, name = "sort")
    @Operation(description = "Get users")
    public Page<UserResponse> getUsers(
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "ACTIVE") UserStatus status,
            @Parameter(hidden = true) Pageable pageable
    ) {
        return userService.getUsers(search, status, pageable);
    }

    @PatchMapping("/{userId}")
    @Operation(description = "Approve user")
    public ResponseEntity<Void> updateUserStatus(
            @Parameter(hidden = true) @RequestAttribute(USER_ID_ATTRIBUTE) Long updatedBy,
            @PathVariable Long userId,
            @RequestBody @Valid StatusUpdateRequest request
    ) {
        userService.updateUserStatus(request, userId, updatedBy);
        return ResponseEntity.noContent()
                .build();
    }

    @DeleteMapping("/{userId}")
    @Operation(description = "Delete user")
    public ResponseEntity<Void> deleteUser(
            @Parameter(hidden = true) @RequestAttribute(USER_ID_ATTRIBUTE) Long updatedBy,
            @PathVariable Long userId
    ) {
        userService.deleteUser(userId, updatedBy);
        return ResponseEntity.noContent()
                .build();
    }
}
