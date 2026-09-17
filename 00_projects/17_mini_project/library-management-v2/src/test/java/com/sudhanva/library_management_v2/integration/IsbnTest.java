package com.sudhanva.library_management_v2.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestConstructor;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor 
@SpringBootTest
@Testcontainers
@TestConstructor (autowireMode = TestConstructor.AutowireMode.ALL)
public class IsbnTest {
    

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = 
            new PostgreSQLContainer<>("postgres:16-alpine");



    private final JdbcTemplate jdbcTemplate;




    // To Test For Isbn is in the container after migration
    @Test 
    void shouldHaveIsbnColumnAfterMigration(){


        Integer count = jdbcTemplate.queryForObject(
            """
        SELECT COUNT(*)
        FROM information_schema.columns
        WHERE table_schema = 'public'
          AND table_name = 'book'
          AND column_name = 'isbn'
        """,
        Integer.class    
        );


        assertEquals(1, count);

    }

}
