package com.erp.core_module.controller;

import com.erp.communication_client.CommunicationClient;
import com.erp.communication_data.model.EmailModel;
import com.erp.core_data.model.response.RegistrationResponse;
import com.erp.core_module.JsonAssertHelper;
import com.erp.core_module.config.TestPersistenceConfig;
import com.erp.core_module.entity.UserEntity;
import com.erp.core_module.repository.ConfirmationRepository;
import com.erp.core_module.repository.UserRepository;
import com.erp.media_client.MediaClient;
import com.erp.shared.util.LocalDateTimeUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.stream.Stream;

import static com.erp.shared_data.constant.ExceptionConstants.CODE_RESEND_NOT_AVAILABLE;
import static com.erp.shared_data.constant.ExceptionConstants.EMAIL_EXISTS;
import static com.erp.shared_data.constant.ExceptionConstants.PHONE_EXISTS;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {"server.port=0", "eureka.client.enabled=false"})
@ContextConfiguration(classes = {TestPersistenceConfig.class})
@AutoConfigureMockMvc(addFilters = false)
@RequiredArgsConstructor
@Transactional
class RegistrationTest {

    private static final String USER_REGISTRATION_URL = "/v1/users/registrations";
    private static final String CODE_RESEND_TIME = "2023-02-01T11:38:45.300";
    private static final String MOCK_TIME = "2023-02-01T11:33:45";

    @Autowired
    private ConfirmationRepository confirmationRepository;
    @Autowired
    private CommunicationClient communicationClient;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MediaClient mediaClient;
    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldRegisterUser() throws Exception {
        String requestPath = "data/json/registration/UserRegistrationValidRequest.json";
        MockMultipartFile photo = buildPhoto();

        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.PUT, USER_REGISTRATION_URL)
                        .file(buildRequest(requestPath))
                        .file(photo)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
                .andExpect(status().isOk());

        JsonAssertHelper.assertResponseAndJsonFile(
                result,
                "data/json/registration/UserRegistrationResponse.json",
                "id", "photoUrl"
        );

        Long userId = objectMapper.readValue(
                result.andReturn().getResponse().getContentAsString(),
                RegistrationResponse.class
        ).getId();

        Optional<UserEntity> user = userRepository.findById(userId);

        Assertions.assertTrue(confirmationRepository.findByUserId(userId).isPresent());
        Assertions.assertTrue(user.isPresent());
        Assertions.assertNotNull(user.get().getPhotoUrl());

        JsonAssertHelper.assertJsons(
                JsonAssertHelper.readJsonFromResources("data/json/registration/UserEntity.json"),
                objectMapper.writeValueAsString(user.get()),
                "id", "photoUrl", "password", "createdAt", "updatedAt", "createdBy", "updatedBy"
        );

        Mockito.verify(communicationClient).sendEvent(any(EmailModel.class));
        Mockito.verify(mediaClient).uploadFile(eq(photo), anyString());
    }

    @Test
    void shouldRegisterUserWithoutPhoto() throws Exception {
        String requestPath = "data/json/registration/UserRegistrationValidRequest.json";

        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.PUT, USER_REGISTRATION_URL)
                        .file(buildRequest(requestPath))
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
                .andExpect(status().isOk());

        JsonAssertHelper.assertResponseAndJsonFile(
                result,
                "data/json/registration/UserRegistrationResponse.json",
                "id", "photoUrl"
        );

        Long userId = objectMapper.readValue(
                result.andReturn().getResponse().getContentAsString(),
                RegistrationResponse.class
        ).getId();

        Optional<UserEntity> user = userRepository.findById(userId);

        Assertions.assertTrue(confirmationRepository.findByUserId(userId).isPresent());
        Assertions.assertTrue(user.isPresent());
        Assertions.assertNull(user.get().getPhotoUrl());

        JsonAssertHelper.assertJsons(
                JsonAssertHelper.readJsonFromResources("data/json/registration/UserEntity.json"),
                objectMapper.writeValueAsString(user.get()),
                "id", "photoUrl", "password", "createdAt", "updatedAt", "createdBy", "updatedBy"
        );

        Mockito.verify(communicationClient).sendEvent(any(EmailModel.class));
        Mockito.verifyNoInteractions(mediaClient);
    }

    @ParameterizedTest
    @MethodSource("getRegistrationParams")
    @Sql("classpath:data/sql/registration/active_user.sql")
    void shouldNotRegisterUserThenBadRequest(String requestPath, String message) throws Exception {
        MockMultipartFile photo = buildPhoto();

        mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.PUT, USER_REGISTRATION_URL)
                        .file(buildRequest(requestPath))
                        .file(photo)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail", Matchers.containsString(message)));

        Mockito.verifyNoInteractions(communicationClient);
        Mockito.verifyNoInteractions(mediaClient);
    }

    @Test
    @Sql(scripts = {
            "classpath:data/sql/registration/email_confirmation_user.sql",
            "classpath:data/sql/registration/confirmation.sql"
    })
    void shouldNotRegisterUserThenBadRequestWitInvalidResendTime() throws Exception {
        String requestPath = "data/json/registration/UserRegistrationValidRequest.json";
        MockMultipartFile photo = buildPhoto();

        try (MockedStatic<LocalDateTimeUtil> utilities = Mockito.mockStatic(LocalDateTimeUtil.class)) {
            utilities.when(LocalDateTimeUtil::getInstantNow)
                    .thenReturn(LocalDateTime.parse(MOCK_TIME));
            mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.PUT, USER_REGISTRATION_URL)
                            .file(buildRequest(requestPath))
                            .file(photo)
                            .accept(MediaType.APPLICATION_JSON)
                            .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath(
                            "$.detail",
                            Matchers.containsString(String.format(CODE_RESEND_NOT_AVAILABLE, CODE_RESEND_TIME))
                    ));
        }

        Mockito.verifyNoInteractions(communicationClient);
        Mockito.verifyNoInteractions(mediaClient);
    }

    @SneakyThrows
    private static MockMultipartFile buildPhoto() {
        byte[] fileForSavingInBytes = IOUtils.toByteArray(new ClassPathResource("data/file/photo.jpeg").getInputStream());

        return new MockMultipartFile(
                "photo",
                "data/file/photo.jpeg",
                MediaType.APPLICATION_OCTET_STREAM_VALUE,
                fileForSavingInBytes
        );
    }

    private static MockMultipartFile buildRequest(String requestPath) throws IOException {
        byte[] fileForSavingInBytes = IOUtils.toByteArray(new ClassPathResource(requestPath).getInputStream());

        return new MockMultipartFile(
                "request",
                "data/json/registration/UserRegistrationValidRequest.json",
                MediaType.APPLICATION_JSON_VALUE,
                fileForSavingInBytes
        );
    }

    private static Stream<Arguments> getRegistrationParams() {
        return Stream.of(
                Arguments.of(
                        "data/json/registration/UserRegistrationValidRequest.json",
                        String.format(EMAIL_EXISTS, "user@gmail.com")
                ),
                Arguments.of(
                        "data/json/registration/UserRegistrationInvalidPhoneRequest.json",
                        String.format(PHONE_EXISTS, "+380453345777")
                )
        );
    }
}
