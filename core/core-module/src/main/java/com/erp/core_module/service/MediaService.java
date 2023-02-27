package com.erp.core_module.service;

import com.erp.core_data.enumeration.MediaType;
import com.erp.media_client.MediaClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class MediaService {

    private final MediaClient mediaClient;

    public String uploadPhoto(MultipartFile file, MediaType mediaType, Long id) {
        return uploadPhoto(file, mediaType, id, null);
    }

    public String uploadPhoto(MultipartFile file, MediaType mediaType, Long id, String existingUrl) {
        if (Objects.nonNull(file)) {
            String photoUrl = String.format("%s/%s", mediaType.name().toLowerCase(), id);
            mediaClient.uploadFile(file, photoUrl);
            return photoUrl;
        } else if (Objects.nonNull(existingUrl)) {
            mediaClient.deleteFile(existingUrl);
        }
        return null;
    }
}
