package com.erp.media_core.service;

import com.erp.media_core.config.property.AWSProperties;
import com.erp.shared_data.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

import static com.erp.shared_data.constant.ExceptionConstants.UNABLE_TO_LOAD_FILE_EXCEPTION;

@Slf4j
@Service
@RequiredArgsConstructor
public class MediaService {

    private final AwsS3Service awsS3Service;
    private final AWSProperties awsProperties;

    public void uploadFile(MultipartFile file, String fileName) {
        try {
            awsS3Service.uploadFile(file.getInputStream(), fileName, awsProperties.getBucket());
        } catch (IOException e) {
            throw new BadRequestException(UNABLE_TO_LOAD_FILE_EXCEPTION);
        }
    }

    public void deleteFile(String url) {
        awsS3Service.deleteFile(url, awsProperties.getBucket());
    }

    public InputStream getFile(String url) {
        return awsS3Service.getFileFromBucket(url, awsProperties.getBucket());
    }
}
