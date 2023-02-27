package com.erp.gateway.controller;

import com.erp.core_data.model.request.ConfirmationRequest;
import com.erp.core_data.model.request.RegistrationRequest;
import com.erp.core_data.model.response.RegistrationResponse;
import com.erp.gateway.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "Registration API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/users")
@SecurityRequirement(name = "erp")
public class RegistrationController {

    private final UserService userService;

    @PutMapping(
            path =  "/registrations",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(description = "Registration")
    public RegistrationResponse register(
            @RequestPart @Valid RegistrationRequest request,
            @RequestPart(required = false) MultipartFile photo
    ){
        return userService.register(request, photo);
    }

    @PostMapping("/{userId}/confirmations")
    @Operation(description = "Email conformation")
    public ResponseEntity<Void> confirmEmail(
            @PathVariable Long userId,
            @RequestBody @Valid ConfirmationRequest request
    ) {
        userService.confirmEmail(userId, request);
        return ResponseEntity.noContent()
                .build();
    }
}
