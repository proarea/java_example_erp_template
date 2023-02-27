package com.erp.communication_core.service;

import com.erp.communication_data.model.EmailModel;

public interface EmailSender {

    void sendEmails(EmailModel request);
}
