package com.erp.gateway.controller;

import com.erp.gateway.service.MediaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

@Tag(name = "Media API")
@RestController
@RequestMapping("/v1/media")
@RequiredArgsConstructor
@SecurityRequirement(name = "erp")
public class MediaController {

    private final MediaService mediaService;

    @GetMapping(produces = {
            MediaType.APPLICATION_OCTET_STREAM_VALUE,
            MediaType.APPLICATION_JSON_VALUE
    })
    @Operation(description = "Get file by url")
    public ResponseEntity<StreamingResponseBody> getFile(@RequestParam String url) {
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(mediaService.getFile(url)::transferTo);
    }
}
