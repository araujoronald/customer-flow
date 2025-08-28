package com.github.araujoronald.infra.adapters.repositories;

import com.github.araujoronald.application.ports.CustomerRepository;
import com.github.araujoronald.domain.model.Customer;
import com.github.araujoronald.domain.model.CustomerQualifier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class CustomerRepositoryMemoryTest {

    private CustomerRepository customerRepository;

    @BeforeEach
    void setUp() {
        // Para cada teste, criamos uma nova instância do repositório em memória
        // para garantir que os testes sejam independentes.
        customerRepository = new CustomerRepositoryMemory();
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
        assertTrue(foundCustomerOpt.isPresent(), "Customer should be found after being saved");
        Customer foundCustomer = foundCustomerOpt.get();
        assertEquals(customer.id(), foundCustomer.id());
        assertEquals("John Doe", foundCustomer.name());
    }

    @Test
    @DisplayName("Should return an empty Optional when customer is not found")
    void shouldReturnEmptyOptionalWhenCustomerNotFound() {
        // Given
        UUID nonExistentId = UUID.randomUUID();

        // When
        Optional<Customer> foundCustomerOpt = customerRepository.find(nonExistentId);

        // Then
        assertFalse(foundCustomerOpt.isPresent(), "Should not find a customer with a non-existent ID");
    }

    @Test
    @DisplayName("Should return all saved customers")
    void shouldReturnAllSavedCustomers() {
        // Given
        Customer customer1 = Customer.create("Alice", "alice@example.com", "+111", CustomerQualifier.DEFAULT);
        Customer customer2 = Customer.create("Bob", "bob@example.com", "+222", CustomerQualifier.VIP);
        customerRepository.save(customer1);
        customerRepository.save(customer2);

        // When
        Collection<Customer> allCustomers = customerRepository.findAll();

        // Then
        assertEquals(2, allCustomers.size());
        assertTrue(allCustomers.contains(customer1));
        assertTrue(allCustomers.contains(customer2));
    }
}