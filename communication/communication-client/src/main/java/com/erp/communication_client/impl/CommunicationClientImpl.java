package com.erp.communication_client.impl;

import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.amazonaws.services.sqs.model.SendMessageResult;
import com.erp.communication_client.CommunicationClient;
import com.erp.communication_data.model.EmailModel;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CommunicationClientImpl implements CommunicationClient {

    private final AmazonSQSAsync amazonSQSAsync;
    private final ObjectMapper objectMapper;

    @Value("${aws.sqs}")
    private String queue;

    @SneakyThrows
    @Override
    public void sendEvent(EmailModel event) {

        String eventAsString = objectMapper.writeValueAsString(event);
        SendMessageResult sendMessageResult = amazonSQSAsync.sendMessage(queue, eventAsString);

        log.debug("Send event: {}", sendMessageResult.toString());
    }
}
