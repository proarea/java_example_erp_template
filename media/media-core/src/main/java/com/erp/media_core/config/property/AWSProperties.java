package com.erp.media_core.config.property;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Setter
@Getter
@Configuration
@ConfigurationProperties(prefix = "aws.s3")
public class AWSProperties {
    private String bucket;
    private String key;
    private String secret;
    private String region;
    private String serviceEndpoint;
}
