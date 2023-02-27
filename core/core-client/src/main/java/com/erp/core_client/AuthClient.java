package com.erp.core_client;

import com.erp.core_data.model.AuthTokenModel;
import com.erp.core_data.model.response.UserDetailsResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;


@FeignClient("${core.name}")
public interface AuthClient {

    @GetMapping(path = "${core.userDetailsUrl}")
    UserDetailsResponse getUserDetails(@RequestParam String email);

    @PutMapping(path = "${core.tokenUrl}")
    void addToken(@PathVariable Long userId, @RequestBody AuthTokenModel request);

    @GetMapping(path = "${core.tokenUrl}")
    AuthTokenModel getToken(@PathVariable Long userId);
}
