package com.erp.media_core.controller;

import com.erp.media_core.service.MediaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/media")
public class MediaController {

    private final MediaService mediaService;

    @GetMapping(produces = {
            MediaType.APPLICATION_OCTET_STREAM_VALUE,
            MediaType.APPLICATION_JSON_VALUE
    })
    public StreamingResponseBody getFile(@RequestParam String url) {
        return mediaService.getFile(url)::transferTo;
    }

    @PutMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> uploadFile(
            @RequestParam String url,
            @RequestParam MultipartFile file
    ) {
        mediaService.uploadFile(file, url);
        return ResponseEntity.noContent()
                .build();
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteFile(
            @RequestParam String url
    ) {
        mediaService.deleteFile(url);
        return ResponseEntity.noContent()
                .build();
    }
}
