package com.sudhanva.library_management_v2.performence;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.PostgreSQLContainer;


// Since This is simple Configuration
// we are avodiing uneccesay proxy overhead
@TestConfiguration(proxyBeanMethods = false)
public class PerfContainerConfig {

    @Bean
    @ServiceConnection 
    PostgreSQLContainer<?> postgres() {
        return new PostgreSQLContainer<>("postgres:16-alpine")
                // This is the Database name inside postgress Container
                .withDatabaseName("lms_perf");
    
    }
}
