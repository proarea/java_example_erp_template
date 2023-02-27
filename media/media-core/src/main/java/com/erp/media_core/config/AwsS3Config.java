package com.erp.media_core.config;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.SSEAlgorithm;
import com.amazonaws.services.s3.model.ServerSideEncryptionByDefault;
import com.amazonaws.services.s3.model.ServerSideEncryptionConfiguration;
import com.amazonaws.services.s3.model.ServerSideEncryptionRule;
import com.amazonaws.services.s3.model.SetBucketEncryptionRequest;
import com.erp.media_core.config.property.AWSProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;

@Configuration
public class AwsS3Config {

    @Bean
    public AmazonS3Client amazonS3(AWSProperties awsProperties) {
        ServerSideEncryptionByDefault serverSideEncryptionByDefault = new ServerSideEncryptionByDefault()
                .withSSEAlgorithm(SSEAlgorithm.KMS);
        ServerSideEncryptionRule rule = new ServerSideEncryptionRule()
                .withApplyServerSideEncryptionByDefault(serverSideEncryptionByDefault)
                .withBucketKeyEnabled(true);

        AWSCredentials awsCredentials = new BasicAWSCredentials(awsProperties.getKey(), awsProperties.getSecret());
        AWSCredentialsProvider credentialsProvider = new AWSStaticCredentialsProvider(awsCredentials);
        AwsClientBuilder.EndpointConfiguration endpointConfiguration = new AwsClientBuilder
                .EndpointConfiguration(awsProperties.getServiceEndpoint(), awsProperties.getRegion());

        AmazonS3 s3client = AmazonS3ClientBuilder
                .standard()
                .withEndpointConfiguration(endpointConfiguration)
                .withPathStyleAccessEnabled(true)
                .withCredentials(credentialsProvider)
                .build();

        ServerSideEncryptionConfiguration serverSideEncryptionConfiguration =
                new ServerSideEncryptionConfiguration().withRules(Collections.singleton(rule));

        SetBucketEncryptionRequest setBucketEncryptionRequest = new SetBucketEncryptionRequest()
                .withServerSideEncryptionConfiguration(serverSideEncryptionConfiguration)
                .withBucketName(awsProperties.getBucket());

        if (!s3client.doesBucketExistV2(awsProperties.getBucket())) {
            s3client.createBucket(awsProperties.getBucket());
        }

        s3client.setBucketEncryption(setBucketEncryptionRequest);

        return (AmazonS3Client) s3client;
    }
}
