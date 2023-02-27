package com.erp.media_client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

@FeignClient(value = "${media.name}", path = "${media.baseUrl}")
public interface MediaClient {

    @GetMapping(
            produces = {
                    MediaType.APPLICATION_OCTET_STREAM_VALUE,
                    MediaType.APPLICATION_JSON_VALUE
            }
    )
    ByteArrayResource getFile(@RequestParam String url);

    @PutMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<Void> uploadFile(@RequestPart MultipartFile file, @RequestParam String url);

    @DeleteMapping(path = "${media.baseUrl}")
    ResponseEntity<Void> deleteFile(@RequestParam String url);
}
