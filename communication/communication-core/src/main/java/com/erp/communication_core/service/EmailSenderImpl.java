package com.erp.communication_core.service;

import com.erp.communication_data.model.EmailModel;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailSenderImpl implements EmailSender {

    private final SpringTemplateEngine thymeleafTemplateEngine;
    private final JavaMailSender emailSender;

    @Override
    public void sendEmails(EmailModel request) {
        MimeMessage message = emailSender.createMimeMessage();
        try {
            message.setSubject(request.getEmailType().getSubject());
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setText(buildEmailMessage(request), true);
            helper.setTo(request.getTo().toArray(String[]::new));
            emailSender.send(message);
        } catch (MessagingException e) {
            log.warn(e.toString());
        }

        log.debug("Emails with subject {} sent to: {}", request.getEmailType().getSubject(), request.getTo());
    }

    private String buildEmailMessage(EmailModel request) {
        Context thymeleafContext = new Context();
        thymeleafContext.setVariables(request.getTemplateModel());

        return thymeleafTemplateEngine.process(
                request.getEmailType().getFileName(),
                thymeleafContext
        );
    }
}
