package com.erp.media_core.controller;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.erp.media_core.config.property.AWSProperties;
import com.erp.shared_data.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.io.IOException;
import java.io.InputStream;

import static com.erp.shared_data.constant.ExceptionConstants.FILE_NOT_FOUND;
import static com.erp.shared_data.constant.ExceptionConstants.UNABLE_TO_LOAD_FILE_EXCEPTION;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {"server.port=0", "eureka.client.enabled=false"})
@AutoConfigureMockMvc(addFilters = false)
@ContextConfiguration
@PropertySource("classpath:application-test.yml")
@RequiredArgsConstructor
class MediaTest {

    private static final String MEDIA_URL = "/v1/media";
    private static final String PHOTO_URL = "photo/1";
    private final static String PARAM = "url";

    @MockBean
    private AmazonS3Client amazonS3Client;
    @MockBean
    private S3Object s3Object;

    @Autowired
    private AWSProperties awsProperties;
    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldGetFile() throws Exception {
        when(s3Object.getObjectContent()).thenReturn(new S3ObjectInputStream(buildInputStream(), null));
        when(amazonS3Client.getObject(awsProperties.getBucket(), PHOTO_URL)).thenReturn(s3Object);
        when(amazonS3Client.doesObjectExist(awsProperties.getBucket(), PHOTO_URL)).thenReturn(true);

        mockMvc.perform(get(MEDIA_URL).param(PARAM, PHOTO_URL)
                        .accept(MediaType.APPLICATION_OCTET_STREAM))
                .andExpect(status().isOk())
                .andExpect(content().bytes(buildFileInBytes()));

        Mockito.verify(amazonS3Client).doesObjectExist(awsProperties.getBucket(), PHOTO_URL);
        Mockito.verify(amazonS3Client).getObject(awsProperties.getBucket(), PHOTO_URL);
        Mockito.verify(s3Object).getObjectContent();
    }

    @Test
    void shouldNotGetFileThenNotFound() throws Exception {
        when(amazonS3Client.doesObjectExist(awsProperties.getBucket(), PHOTO_URL)).thenReturn(false);

        mockMvc.perform(get(MEDIA_URL).param(PARAM, PHOTO_URL)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail", Matchers.containsString(String.format(FILE_NOT_FOUND, PHOTO_URL))));

        Mockito.verify(amazonS3Client).doesObjectExist(awsProperties.getBucket(), PHOTO_URL);
        Mockito.verifyNoInteractions(s3Object);
    }

    @Test
    void shouldDeleteFile() throws Exception {
        mockMvc.perform(delete(MEDIA_URL).param(PARAM, PHOTO_URL)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        Mockito.verify(amazonS3Client).deleteObject(awsProperties.getBucket(), PHOTO_URL);
    }

    @Test
    void shouldUploadFile() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.PUT, MEDIA_URL).file(buildPhoto())
                        .param(PARAM, PHOTO_URL)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
                .andExpect(status().isNoContent());

        Mockito.verify(amazonS3Client).putObject(
                eq(awsProperties.getBucket()),
                eq(PHOTO_URL),
                any(InputStream.class),
                any(ObjectMetadata.class)
        );
    }

    @Test
    void shouldNotUploadFileThenBadRequest() throws Exception {
        MockMultipartFile photo = buildPhoto();

        when(amazonS3Client.putObject(
                eq(awsProperties.getBucket()),
                eq(PHOTO_URL),
                any(InputStream.class),
                any(ObjectMetadata.class))
        ).thenThrow(new BadRequestException(UNABLE_TO_LOAD_FILE_EXCEPTION));

        mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.PUT, MEDIA_URL).file(photo)
                        .param(PARAM, PHOTO_URL)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail", Matchers.containsString(UNABLE_TO_LOAD_FILE_EXCEPTION)));

        Mockito.verify(amazonS3Client).putObject(
                eq(awsProperties.getBucket()),
                eq(PHOTO_URL),
                any(InputStream.class),
                any(ObjectMetadata.class)
        );
    }

    private InputStream buildInputStream() throws IOException {
        return new ClassPathResource("data/file/photo.jpeg").getInputStream();
    }

    private byte[] buildFileInBytes() throws IOException {
        return IOUtils.toByteArray(new ClassPathResource("data/file/photo.jpeg").getInputStream());
    }

    @SneakyThrows
    private static MockMultipartFile buildPhoto() {
        byte[] fileForSavingInBytes = IOUtils.toByteArray(new ClassPathResource("data/file/photo.jpeg").getInputStream());

        return new MockMultipartFile(
                "file",
                "data/file/photo.jpeg",
                MediaType.APPLICATION_OCTET_STREAM_VALUE,
                fileForSavingInBytes
        );
    }
}
