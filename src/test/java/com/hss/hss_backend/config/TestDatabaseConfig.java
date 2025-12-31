package com.hss.hss_backend.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

/**
 * TestContainers configuration for integration tests
 * Provides PostgreSQL container for testing with real database
 */
@TestConfiguration
@Profile("test")
public class TestDatabaseConfig {

    private static final PostgreSQLContainer<?> postgresContainer;

    static {
        postgresContainer = new PostgreSQLContainer<>(DockerImageName.parse("postgres:16-alpine"))
                .withDatabaseName("hss_test")
                .withUsername("test")
                .withPassword("test")
                .withReuse(true); // Reuse container across tests for faster execution
        
        postgresContainer.start();
        
        // Shutdown hook to close container when JVM exits
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (postgresContainer.isRunning()) {
                postgresContainer.stop();
            }
        }));
    }

    @Bean
    @Primary
    public PostgreSQLContainer<?> postgresContainer() {
        return postgresContainer;
    }

    /**
     * Get JDBC URL for test database
     */
    public static String getJdbcUrl() {
        return postgresContainer.getJdbcUrl();
    }

    /**
     * Get username for test database
     */
    public static String getUsername() {
        return postgresContainer.getUsername();
    }

    /**
     * Get password for test database
     */
    public static String getPassword() {
        return postgresContainer.getPassword();
    }
}

