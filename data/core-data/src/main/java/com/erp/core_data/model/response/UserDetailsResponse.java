package com.erp.core_data.model.response;

import com.erp.core_data.enumeration.Role;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDetailsResponse {
    private Long userId;
    private String password;
    private Role role;
}
