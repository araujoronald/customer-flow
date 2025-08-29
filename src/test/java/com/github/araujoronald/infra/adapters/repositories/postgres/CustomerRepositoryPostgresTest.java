package com.github.araujoronald.infra.adapters.repositories.postgres;

import com.github.araujoronald.application.ports.CustomerRepository;
import com.github.araujoronald.domain.model.Customer;
import com.github.araujoronald.domain.model.CustomerQualifier;
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
import java.sql.Statement;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Disabled
@Testcontainers
class CustomerRepositoryPostgresTest {

    @Container
    private static final PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:15-alpine");

    private CustomerRepository customerRepository;

    @BeforeEach
    void setUp() throws Exception {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(postgresContainer.getJdbcUrl());
        config.setUsername(postgresContainer.getUsername());
        config.setPassword(postgresContainer.getPassword());
        DataSource dataSource = new HikariDataSource(config);

        try (Connection conn = dataSource.getConnection(); Statement stmt = conn.createStatement()) {
            stmt.execute("CREATE TABLE IF NOT EXISTS customers (id UUID PRIMARY KEY, name VARCHAR(255), email VARCHAR(255) UNIQUE, phone VARCHAR(255), qualifier VARCHAR(50))");
            stmt.execute("TRUNCATE TABLE customers");
        }

        customerRepository = new CustomerRepositoryPostgres(dataSource);
    }

    @Test
    @DisplayName("Should save a customer and find it by ID")
    void shouldSaveAndFindCustomerById() {
        // Given
        Customer customer = Customer.create("John Doe", "john.doe@example.com", "+15551234567", CustomerQualifier.VIP);

        // When
        customerRepository.save(customer);
        Optional<Customer> foundCustomerOpt = customerRepository.find(customer.id());

        // Then
        assertTrue(foundCustomerOpt.isPresent());
        assertEquals(customer.id(), foundCustomerOpt.get().id());
        assertEquals("John Doe", foundCustomerOpt.get().name());
        assertEquals(CustomerQualifier.VIP, foundCustomerOpt.get().qualifier());
    }

    @Test
    @DisplayName("Should update an existing customer")
    void shouldUpdateExistingCustomer() {
        // Given
        Customer originalCustomer = Customer.create("Jane Doe", "jane.doe@example.com", "+1", CustomerQualifier.DEFAULT);
        customerRepository.save(originalCustomer);

        // When
        Customer updatedCustomer = new Customer(originalCustomer.id(), "Jane D. Updated", "jane.doe@example.com", "+2", CustomerQualifier.EXPRESS);
        customerRepository.save(updatedCustomer);
        Optional<Customer> foundCustomerOpt = customerRepository.find(originalCustomer.id());

        // Then
        assertTrue(foundCustomerOpt.isPresent());
        assertEquals("Jane D. Updated", foundCustomerOpt.get().name());
        assertEquals(CustomerQualifier.EXPRESS, foundCustomerOpt.get().qualifier());
    }
}