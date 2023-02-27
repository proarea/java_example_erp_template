package com.erp.core_data.model.request;

import com.erp.core_data.enumeration.Role;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StatusUpdateRequest {

    @NotNull
    private Boolean approved;

    @NotNull
    private Role role;

    @Hidden
    private Long updatedBy;
}
