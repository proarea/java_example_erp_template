package com.erp.communication_data.model;

import com.erp.communication_data.enumeration.EmailType;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@ToString
public class EmailModel {
    private List<String> to;
    private Map<String, Object> templateModel;
    private EmailType emailType;
}
