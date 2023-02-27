package com.erp.core_module.service;

import com.erp.communication_client.CommunicationClient;
import com.erp.communication_data.enumeration.EmailType;
import com.erp.communication_data.model.EmailModel;
import com.erp.core_module.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.erp.core_data.constant.CoreConstants.CODE_PARAM;
import static com.erp.core_data.constant.CoreConstants.USER_EMAIL_PARAM;
import static com.erp.core_data.constant.CoreConstants.USER_FULL_NAME_FORMAT;
import static com.erp.core_data.constant.CoreConstants.USER_FULL_NAME_PARAM;
import static com.erp.core_data.constant.CoreConstants.USER_PHONE_PARAM;

@Service
@RequiredArgsConstructor
public class CommunicationService {

    private final CommunicationClient communicationClient;

    public void sendCodeEmail(String email, String code) {
        EmailModel emailModel = new EmailModel()
                .setTemplateModel(Map.of(CODE_PARAM, code))
                .setEmailType(EmailType.CODE_CONFIRMATION)
                .setTo(Collections.singletonList(email));

        communicationClient.sendEvent(emailModel);
    }

    public void sendWaitingApprovalEmail(UserEntity user, List<String> emails) {
        EmailModel emailModel = new EmailModel()
                .setTemplateModel(buildTemplateModelParams(user))
                .setEmailType(EmailType.WAITING_APPROVAL)
                .setTo(emails);

        communicationClient.sendEvent(emailModel);
    }

    public void sendApproveEmail(String email) {
        EmailModel emailModel = new EmailModel()
                .setTemplateModel(new HashMap<>())
                .setEmailType(EmailType.APPROVED)
                .setTo(Collections.singletonList(email));

        communicationClient.sendEvent(emailModel);
    }

    public void sendDeclineEmail(String email) {
        EmailModel emailModel = new EmailModel()
                .setTemplateModel(new HashMap<>())
                .setEmailType(EmailType.DECLINED)
                .setTo(Collections.singletonList(email));

        communicationClient.sendEvent(emailModel);
    }

    public void sendDeletionEmail(String email) {
        EmailModel emailModel = new EmailModel()
                .setTemplateModel(new HashMap<>())
                .setEmailType(EmailType.DELETED)
                .setTo(Collections.singletonList(email));

        communicationClient.sendEvent(emailModel);
    }

    private Map<String, Object> buildTemplateModelParams(UserEntity user) {
        return Map.of(
                USER_FULL_NAME_PARAM, String.format(USER_FULL_NAME_FORMAT, user.getFirstName(), user.getLastName()),
                USER_EMAIL_PARAM, user.getEmail(),
                USER_PHONE_PARAM, user.getPhone()
        );
    }
}
