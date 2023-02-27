package com.erp.que.config;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.amazonaws.services.sqs.AmazonSQSAsyncClientBuilder;
import com.erp.que.config.property.SQSProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.awspring.cloud.messaging.config.QueueMessageHandlerFactory;
import io.awspring.cloud.messaging.listener.QueueMessageHandler;
import io.awspring.cloud.messaging.listener.SimpleMessageListenerContainer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.handler.annotation.support.PayloadMethodArgumentResolver;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.Collections;

@Configuration
public class SQSConfiguration {

    @Bean
    public AmazonSQSAsync amazonSQS(SQSProperties sqsProperties) {
        return AmazonSQSAsyncClientBuilder.standard()
                .withRegion(sqsProperties.getRegion())
                .withCredentials(
                        new AWSStaticCredentialsProvider(
                                new BasicAWSCredentials(sqsProperties.getKey(), sqsProperties.getSecret())
                        ))
                .build();
    }

    @Bean
    public QueueMessageHandler queueMessageHandler(final AmazonSQSAsync amazonSQS, final ObjectMapper mapper) {
        final QueueMessageHandlerFactory queueMessageHandlerFactory = new QueueMessageHandlerFactory();

        queueMessageHandlerFactory.setAmazonSqs(amazonSQS);
        queueMessageHandlerFactory.setArgumentResolvers(Collections.singletonList(
                new PayloadMethodArgumentResolver(jackson2MessageConverter(mapper)))
        );

        return queueMessageHandlerFactory.createQueueMessageHandler();
    }

    @Bean
    public SimpleMessageListenerContainer simpleMessageListenerContainer(
            final QueueMessageHandler queueMessageHandler,
            AmazonSQSAsync amazonSQS
    ) {
        final SimpleMessageListenerContainer simpleMessageListenerContainer = new SimpleMessageListenerContainer();

        simpleMessageListenerContainer.setAmazonSqs(amazonSQS);
        simpleMessageListenerContainer.setMessageHandler(queueMessageHandler);
        simpleMessageListenerContainer.setTaskExecutor(threadPoolTaskExecutor());

        return simpleMessageListenerContainer;
    }

    private ThreadPoolTaskExecutor threadPoolTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);
        executor.setMaxPoolSize(10);
        executor.initialize();
        return executor;
    }

    private MessageConverter jackson2MessageConverter(final ObjectMapper mapper) {
        final MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();

        converter.setStrictContentTypeMatch(false);
        converter.setObjectMapper(mapper);

        return converter;
    }
}
