package com.erp.communication_core;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.erp.*")
public class CommunicationApp {
    public static void main(String[] args) {
        SpringApplication.run(CommunicationApp.class, args);
    }
}