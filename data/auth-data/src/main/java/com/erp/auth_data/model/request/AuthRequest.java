package com.erp.auth_data.model.request;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class AuthRequest {
    private String email;
    private String password;
}
