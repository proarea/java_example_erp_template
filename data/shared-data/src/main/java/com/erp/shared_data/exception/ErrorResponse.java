package com.erp.shared_data.exception;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

@Getter
@ToString
@NoArgsConstructor
public class ErrorResponse implements Serializable {

    private Integer status;
    private String detail;
    private String message;
    private ErrorMeta meta;

    public ErrorResponse(Integer status, String detail, String message, String path) {
        this.status = status;
        this.detail = detail;
        this.message = message;
        this.meta = new ErrorMeta(path);
    }

    @Data
    @NoArgsConstructor
    private static class ErrorMeta implements Serializable {

        private Long timestamp;
        private String path;

        public ErrorMeta(String path) {
            this.path = path;
            this.timestamp = System.currentTimeMillis();
        }
    }
}
