package com.erp.communication_client;

import com.erp.communication_data.model.EmailModel;

public interface CommunicationClient {
    void sendEvent(EmailModel event);
}
