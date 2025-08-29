package com.github.araujoronald.infra.adapters.repositories.postgres;

import com.github.araujoronald.application.ports.AttendantRepository;
import com.github.araujoronald.domain.model.Attendant;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.junit.Ignore;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@Disabled
@Testcontainers
class AttendantRepositoryPostgresTest {

    @Container
    private static final PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:15-alpine");

    private AttendantRepository attendantRepository;

    @BeforeEach
    void setUp() throws Exception {
        // Create a DataSource pointing to the test container
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(postgresContainer.getJdbcUrl());
        config.setUsername(postgresContainer.getUsername());
        config.setPassword(postgresContainer.getPassword());
        DataSource dataSource = new HikariDataSource(config);

        // Create schema
        try (Connection conn = dataSource.getConnection(); Statement stmt = conn.createStatement()) {
            stmt.execute("CREATE TABLE IF NOT EXISTS attendants (id UUID PRIMARY KEY, name VARCHAR(255), email VARCHAR(255) UNIQUE)");
            stmt.execute("TRUNCATE TABLE attendants");
        }

        // Create a new repository instance with the test DataSource
        attendantRepository = new AttendantRepositoryPostgres(dataSource);
    }

    @Test
    @DisplayName("Should save an attendant and find it by ID")
    void shouldSaveAndFindAttendantById() {
        // Given
        Attendant attendant = Attendant.create("John Attendant", "john.attendant@example.com");

        // When
        attendantRepository.save(attendant);
        Optional<Attendant> foundAttendantOpt = attendantRepository.find(attendant.id());

        // Then
        assertTrue(foundAttendantOpt.isPresent());
        assertEquals(attendant.id(), foundAttendantOpt.get().id());
        assertEquals("John Attendant", foundAttendantOpt.get().name());
    }

    @Test
    @DisplayName("Should save an attendant and find it by email")
    void shouldSaveAndFindAttendantByEmail() {
        // Given
        Attendant attendant = Attendant.create("Jane Attendant", "jane.attendant@example.com");

        // When
        attendantRepository.save(attendant);
        Optional<Attendant> foundAttendantOpt = attendantRepository.findByEmail("jane.attendant@example.com");

        // Then
        assertTrue(foundAttendantOpt.isPresent());
        assertEquals(attendant.id(), foundAttendantOpt.get().id());
    }

    @Test
    @DisplayName("Should return empty Optional when attendant is not found")
    void shouldReturnEmptyWhenNotFound() {
        // When
        Optional<Attendant> notFoundById = attendantRepository.find(UUID.randomUUID());
        Optional<Attendant> notFoundByEmail = attendantRepository.findByEmail("not.found@example.com");

        // Then
        assertFalse(notFoundById.isPresent());
        assertFalse(notFoundByEmail.isPresent());
    }
}