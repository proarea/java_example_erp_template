package com.erp.core_data.model.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConfirmationRequest {

    @NotNull
    private String code;
}
