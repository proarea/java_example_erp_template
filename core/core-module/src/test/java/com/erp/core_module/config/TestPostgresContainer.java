package com.erp.core_module.config;

import org.testcontainers.containers.PostgreSQLContainer;

import java.util.Objects;

public class TestPostgresContainer extends PostgreSQLContainer<TestPostgresContainer> {

    private static TestPostgresContainer container;

    private TestPostgresContainer(String imageVersion) {
        super(imageVersion);
    }

    public static TestPostgresContainer getInstance(String imageVersion) {
        if (Objects.isNull(container)) {
            container = new TestPostgresContainer(imageVersion);
        }
        return container;
    }

    @Override
    public void start() {
        super.start();
    }

    @Override
    public void stop() {
        //do nothing, JVM handles shut down
    }
}
