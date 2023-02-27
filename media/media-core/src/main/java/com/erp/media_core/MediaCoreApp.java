package com.erp.media_core;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.erp.*")
public class MediaCoreApp {

    public static void main(String[] args) {
        SpringApplication.run(MediaCoreApp.class, args);
    }
}
