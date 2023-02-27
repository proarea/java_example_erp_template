package com.erp.gateway.service;

import com.erp.media_client.MediaClient;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import java.io.InputStream;

@Service
@RequiredArgsConstructor
public class MediaService {

    private final MediaClient mediaClient;

    @SneakyThrows
    public InputStream getFile(String url) {
        return mediaClient.getFile(url).getInputStream();
    }
}
