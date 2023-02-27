package com.erp.communication_core.service;

import com.erp.communication_data.model.EmailModel;
import com.erp.que.config.SQSConfiguration;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.ContextConfiguration;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;

@SpringBootTest(properties = {"server.port=0", "eureka.client.enabled=false"})
@ContextConfiguration
@PropertySource("classpath:application-test.yml")
@RequiredArgsConstructor
class CommunicationTest {

    @SpyBean
    private JavaMailSender emailSender;
    @MockBean
    private SQSConfiguration sqs;

    @Autowired
    private CommunicationEventListener listener;
    @Autowired
    private ObjectMapper objectMapper;

    @ParameterizedTest
    @ValueSource(strings = {
            "data/json/ApprovedModel.json",
            "data/json/DeletedModel.json",
            "data/json/DeclinedModel.json",
            "data/json/CodeConfirmationModel.json",
            "data/json/WaitingApprovalModel.json"
    })
    void shouldSendEmail(String path) throws Exception {
        EmailModel emailModel = readJson(path);

        ArgumentCaptor<MimeMessage> mimeMessageArgumentCaptor = ArgumentCaptor.forClass(MimeMessage.class);
        doNothing().when(emailSender).send(any(MimeMessage.class));

        listener.listenQueue(emailModel);

        Mockito.verify(emailSender).send(mimeMessageArgumentCaptor.capture());
        Mockito.verify(emailSender).createMimeMessage();

        Assertions.assertEquals(
                emailModel.getEmailType().getSubject(),
                mimeMessageArgumentCaptor.getValue().getSubject()
        );
    }

    @SneakyThrows
    private EmailModel readJson(String path) {
        return objectMapper.readValue(
                new ClassPathResource(path).getFile(), EmailModel.class
        );
    }
}
