package com.erp.communication_core.service;

import com.erp.communication_data.model.EmailModel;
import io.awspring.cloud.messaging.listener.SqsMessageDeletionPolicy;
import io.awspring.cloud.messaging.listener.annotation.SqsListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommunicationEventListener {

    private final EmailSender emailSender;

    @SqsListener(value = "${aws.sqs}", deletionPolicy = SqsMessageDeletionPolicy.ON_SUCCESS)
    public void listenQueue(EmailModel event) {
        log.debug("Received: {}", event);

        emailSender.sendEmails(event);
    }
}
