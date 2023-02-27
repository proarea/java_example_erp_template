package com.erp.core_module.controller;

import com.erp.communication_client.CommunicationClient;
import com.erp.communication_data.model.EmailModel;
import com.erp.core_data.enumeration.UserStatus;
import com.erp.core_module.JsonAssertHelper;
import com.erp.core_module.config.TestPersistenceConfig;
import com.erp.core_module.entity.TokenEntity;
import com.erp.core_module.entity.UserEntity;
import com.erp.core_module.repository.TokenRepository;
import com.erp.core_module.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.stream.Stream;

import static com.erp.shared_data.constant.ExceptionConstants.INVALID_USER_STATUS;
import static com.erp.shared_data.constant.ExceptionConstants.USER_NOT_EXISTS;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {"server.port=0", "eureka.client.enabled=false"})
@ContextConfiguration(classes = {TestPersistenceConfig.class})
@AutoConfigureMockMvc(addFilters = false)
@RequiredArgsConstructor
@Transactional
class UserTest {

    private static final String USER_URL = "/v1/users";
    private static final String DELETE_USER_URL = "/v1/users/{userId}";
    private static final String UPDATE_STATUS_URL = "/v1/users/{userId}/statuses";
    private static final String UPDATED_BY_PARAM = "updatedBy";
    private static final String STATUS_PARAM = "status";
    private static final String SEARCH_PARAM = "search";
    private static final Long WAITING_APPROVAL_USER_ID = 5L;
    private static final Long INVALID_USER_ID = 999L;
    private static final Long USER_ID = 2L;

    @Autowired
    private CommunicationClient communicationClient;
    @Autowired
    private TokenRepository tokenRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;

    @ParameterizedTest
    @MethodSource("getStatusUpdateParams")
    @Sql("classpath:data/sql/user/user.sql")
    void shouldUpdateUserStatus(String requestPath, String entityPath) throws Exception {
        String requestAsString = JsonAssertHelper.readJsonFromResources(requestPath);

        mockMvc.perform(MockMvcRequestBuilders.put(UPDATE_STATUS_URL, WAITING_APPROVAL_USER_ID)
                        .content(requestAsString)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        Optional<UserEntity> user = userRepository.findById(WAITING_APPROVAL_USER_ID);

        Assertions.assertTrue(user.isPresent());

        JsonAssertHelper.assertJsons(
                JsonAssertHelper.readJsonFromResources(entityPath),
                objectMapper.writeValueAsString(user.get()),
                "createdAt", "updatedAt", "createdBy"
        );
    }

    @ParameterizedTest
    @MethodSource("getInvalidStatusUpdateParams")
    @Sql("classpath:data/sql/user/user.sql")
    void shouldNotUpdateUserStatus(
            Long userId,
            String requestPath,
            ResultMatcher status,
            String message
    ) throws Exception {
        String requestAsString = JsonAssertHelper.readJsonFromResources(requestPath);

        mockMvc.perform(MockMvcRequestBuilders.put(UPDATE_STATUS_URL, userId)
                        .content(requestAsString)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status)
                .andExpect(jsonPath("$.detail", Matchers.containsString(message)));


        Optional<UserEntity> user = userRepository.findById(WAITING_APPROVAL_USER_ID);

        Assertions.assertTrue(user.isPresent());
        Assertions.assertEquals(UserStatus.WAITING_FOR_APPROVING, user.get().getStatus());

        Mockito.verifyNoInteractions(communicationClient);
    }

    @Test
    @Sql(scripts = {
            "classpath:data/sql/user/user.sql",
            "classpath:data/sql/token/token.sql"
    })
    void shouldDeleteUser() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete(DELETE_USER_URL, USER_ID)
                        .param(UPDATED_BY_PARAM, USER_ID.toString())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        Optional<UserEntity> user = userRepository.findById(USER_ID);

        Assertions.assertTrue(user.isPresent());
        Assertions.assertTrue(user.get().isDeleted());
        Assertions.assertEquals(USER_ID, user.get().getUpdatedBy());

        Optional<TokenEntity> token = tokenRepository.findByUserId(USER_ID);
        Assertions.assertTrue(token.isEmpty());

        Mockito.verify(communicationClient).sendEvent(any(EmailModel.class));
    }

    @ParameterizedTest
    @MethodSource("getDeletionParams")
    @Sql(scripts = {
            "classpath:data/sql/user/user.sql",
            "classpath:data/sql/token/token.sql"
    })
    void shouldNotDeleteUserThenNotFound(Long userId, Long updatedBy) throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete(DELETE_USER_URL, userId)
                        .param(UPDATED_BY_PARAM, updatedBy.toString())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath(
                        "$.detail",
                        Matchers.containsString(String.format(USER_NOT_EXISTS, INVALID_USER_ID))
                ));

        Mockito.verifyNoInteractions(communicationClient);
    }

    @ParameterizedTest
    @MethodSource("getUserParams")
    @Sql("classpath:data/sql/user/user.sql")
    void shouldGetUsers(UserStatus status, String responsePath) throws Exception {
        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.get(USER_URL)
                        .param(STATUS_PARAM, status.name())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());

        JsonAssertHelper.assertResponseAndJsonFile(result, responsePath);
    }

    @Test
    @Sql("classpath:data/sql/user/user.sql")
    void shouldGetUsersWithSearch() throws Exception {
        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.get(USER_URL)
                        .param(STATUS_PARAM, UserStatus.ACTIVE.name())
                        .param(SEARCH_PARAM, "DART")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        JsonAssertHelper.assertResponseAndJsonFile(
                result,
                "data/json/user/SearchUsersResponse.json"
        );
    }

    private static Stream<Arguments> getStatusUpdateParams() {
        return Stream.of(
                Arguments.of(
                        "data/json/status_update/ApprovedValidRequest.json",
                        "data/json/status_update/ApprovedUserEntity.json"
                ),
                Arguments.of(
                        "data/json/status_update/DeclinedValidRequest.json",
                        "data/json/status_update/DeclinedUserEntity.json"
                )
        );
    }

    private static Stream<Arguments> getInvalidStatusUpdateParams() {
        return Stream.of(
                Arguments.of(
                        INVALID_USER_ID,
                        "data/json/status_update/ApprovedValidRequest.json",
                        status().isNotFound(),
                        String.format(USER_NOT_EXISTS, INVALID_USER_ID)
                ),
                Arguments.of(
                        WAITING_APPROVAL_USER_ID,
                        "data/json/status_update/StatusUpdateInvalidRequest.json",
                        status().isNotFound(),
                        String.format(USER_NOT_EXISTS, INVALID_USER_ID)
                ),
                Arguments.of(
                        USER_ID,
                        "data/json/status_update/ApprovedValidRequest.json",
                        status().isBadRequest(),
                        String.format(INVALID_USER_STATUS, USER_ID, UserStatus.WAITING_FOR_APPROVING)
                )
        );
    }

    private static Stream<Arguments> getDeletionParams() {
        return Stream.of(
                Arguments.of(INVALID_USER_ID, USER_ID),
                Arguments.of(USER_ID, INVALID_USER_ID)
        );

    }

    private static Stream<Arguments> getUserParams() {
        return Stream.of(
                Arguments.of(
                        UserStatus.ACTIVE,
                        "data/json/user/ActiveUsersResponse.json"
                ),
                Arguments.of(
                        UserStatus.DECLINED,
                        "data/json/user/DeclinedUsersResponse.json"
                ),
                Arguments.of(
                        UserStatus.WAITING_FOR_APPROVING,
                        "data/json/user/WaitingApprovalUsersResponse.json"
                ),
                Arguments.of(
                        UserStatus.EMAIL_CONFIRMATION,
                        "data/json/user/EmailConfirmationUsersResponse.json"
                )
        );
    }
}
