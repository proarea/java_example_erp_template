package com.erp.communication_data.enumeration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EmailType {

    CODE_CONFIRMATION("code-template.html", "Verification email"),
    WAITING_APPROVAL("waiting-approval-template.html", "Registration request received"),
    APPROVED("approved-template.html", "Registration request approved"),
    DECLINED("declined-template.html", "Registration request declined"),
    DELETED("deletion-template.html", "Account deleted");

    private final String fileName;
    private final String subject;
}
