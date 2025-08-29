package com.github.araujoronald.infra.adapters.repositories.postgres;

import com.github.araujoronald.application.ports.TicketRepository;
import com.github.araujoronald.domain.model.*;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.junit.Ignore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Disabled
@Testcontainers
class TicketRepositoryPostgresTest {

    @Container
    private static final PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:15-alpine");

    private TicketRepository ticketRepository;
    private DataSource dataSource;

    private Customer customer;
    private Attendant attendant;

    @BeforeEach
    void setUp() throws Exception {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(postgresContainer.getJdbcUrl());
        config.setUsername(postgresContainer.getUsername());
        config.setPassword(postgresContainer.getPassword());
        dataSource = new HikariDataSource(config);

        try (Connection conn = dataSource.getConnection(); Statement stmt = conn.createStatement()) {
            stmt.execute("CREATE TABLE IF NOT EXISTS customers (id UUID PRIMARY KEY, name VARCHAR(255), email VARCHAR(255) UNIQUE, phone VARCHAR(255), qualifier VARCHAR(50))");
            stmt.execute("CREATE TABLE IF NOT EXISTS attendants (id UUID PRIMARY KEY, name VARCHAR(255), email VARCHAR(255) UNIQUE)");
            stmt.execute("CREATE TABLE IF NOT EXISTS tickets (id UUID PRIMARY KEY, status VARCHAR(50), priority INT, created TIMESTAMP, start TIMESTAMP, \"end\" TIMESTAMP, description TEXT, cancellation_reason TEXT, customer_id UUID REFERENCES customers(id), attendant_id UUID REFERENCES attendants(id))");
            stmt.execute("TRUNCATE TABLE tickets, customers, attendants RESTART IDENTITY CASCADE");
        }

        ticketRepository = new TicketRepositoryPostgres(dataSource);

        // Setup prerequisite data
        customer = Customer.create("Test Customer", "cust@test.com", "+1", CustomerQualifier.VIP);
        attendant = Attendant.create("Test Attendant", "att@test.com");

        try (Connection conn = dataSource.getConnection()) {
            try (PreparedStatement pstmt = conn.prepareStatement("INSERT INTO customers (id, name, email, phone, qualifier) VALUES (?, ?, ?, ?, ?)")) {
                pstmt.setObject(1, customer.id());
                pstmt.setString(2, customer.name());
                pstmt.setString(3, customer.email());
                pstmt.setString(4, customer.phone());
                pstmt.setString(5, customer.qualifier().name());
                pstmt.executeUpdate();
            }
            try (PreparedStatement pstmt = conn.prepareStatement("INSERT INTO attendants (id, name, email) VALUES (?, ?, ?)")) {
                pstmt.setObject(1, attendant.id());
                pstmt.setString(2, attendant.name());
                pstmt.setString(3, attendant.email());
                pstmt.executeUpdate();
            }
        }
    }

    @Test
    @DisplayName("Should save a ticket and find it by ID")
    void shouldSaveAndFindTicketById() {
        // Given
        Ticket ticket = Ticket.create(TicketStatus.PENDING, 5, customer, attendant);
        ticket.start();
        ticket.addDescription("Test description");

        // When
        ticketRepository.save(ticket);
        Optional<Ticket> foundTicketOpt = ticketRepository.find(ticket.getId());

        // Then
        assertTrue(foundTicketOpt.isPresent());
        Ticket foundTicket = foundTicketOpt.get();
        assertEquals(ticket.getId(), foundTicket.getId());
        assertEquals(TicketStatus.IN_PROGRESS, foundTicket.getStatus());
        assertEquals("Test description", foundTicket.getDescription());
        assertEquals(customer.id(), foundTicket.getCustomer().id());
        assertEquals(attendant.id(), foundTicket.getAttendant().id());
    }
}