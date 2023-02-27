package com.erp.shared_data.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FeignClientException extends RuntimeException {
    private final ErrorResponse errorResponse;
}
