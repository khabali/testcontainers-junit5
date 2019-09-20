package io.github.khabali;

import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testcontainers.containers.JdbcDatabaseContainer;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(DatabaseInvocationContextProvider.class)
class MyIntegrationTest {

    @TestTemplate
    void myAwesomeTest(JdbcDatabaseContainer database) {

        // write test relying on the database you get as an argument
        assertTrue(database.isRunning());
        assertNotNull(database.getJdbcUrl());
        assertNotNull(database.getUsername());
        assertNotNull(database.getPassword());
        assertNotNull(database.getDriverClassName());

    }

}
