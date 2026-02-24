package com.hss.hss_backend.base;

import com.hss.hss_backend.config.TestDatabaseConfig;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;

/**
 * Base class for integration tests
 * Configures TestContainers PostgreSQL and test profile
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Transactional
public abstract class BaseIntegrationTest {

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        // Override datasource properties with TestContainers values
        registry.add("spring.datasource.url", TestDatabaseConfig::getJdbcUrl);
        registry.add("spring.datasource.username", TestDatabaseConfig::getUsername);
        registry.add("spring.datasource.password", TestDatabaseConfig::getPassword);
        
        // Disable Cloud SQL for tests
        registry.add("spring.cloud.gcp.sql.enabled", () -> false);
        
        // Disable Flyway validation for faster tests
        registry.add("spring.flyway.validate-on-migrate", () -> false);
    }

    @BeforeEach
    protected void setUp() {
        // Common setup for all integration tests
        // Can be overridden in subclasses
    }
}

