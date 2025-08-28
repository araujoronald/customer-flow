package com.github.araujoronald.application.useCases;

import com.github.araujoronald.application.ports.CreateCustomer;
import com.github.araujoronald.application.ports.CustomerRepository;
import com.github.araujoronald.domain.model.Customer;
import com.github.araujoronald.domain.model.CustomerQualifier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateCustomerImplTest {

    @Mock
    private CustomerRepository customerRepository;

    private CreateCustomer createCustomer;

    @Captor
    private ArgumentCaptor<Customer> customerArgumentCaptor;

    @BeforeEach
    void setUp() {
        createCustomer = new CreateCustomerImpl(customerRepository);
    }

    @Test
    @DisplayName("Should create a customer successfully and return its ID")
    void shouldCreateCustomerSuccessfully() {
        // Given
        var input = new CreateCustomer.Input(
                "John Doe",
                "john.doe@example.com",
                "+15551234567",
                CustomerQualifier.VIP
        );

        UUID expectedId = UUID.randomUUID();
        Customer savedCustomer = new Customer(expectedId, input.name(), input.email(), input.phone(), input.qualifier());

        when(customerRepository.save(any(Customer.class))).thenReturn(savedCustomer);

        // When
        CreateCustomer.Output output = createCustomer.execute(input);

        // Then
        verify(customerRepository).save(customerArgumentCaptor.capture());
        Customer capturedCustomer = customerArgumentCaptor.getValue();

        assertNotNull(output);
        assertEquals(expectedId, output.customerId());

        assertEquals(input.name(), capturedCustomer.name());
        assertEquals(input.email(), capturedCustomer.email());
        assertEquals(input.phone(), capturedCustomer.phone());
        assertEquals(input.qualifier(), capturedCustomer.qualifier());
    }

    @Test
    @DisplayName("Should propagate exception when repository fails to save")
    void shouldPropagateExceptionWhenRepositoryFails() {
        // Given
        var input = new CreateCustomer.Input("Jane Doe", "jane.doe@example.com", "+15557654321", CustomerQualifier.DEFAULT);
        when(customerRepository.save(any(Customer.class))).thenThrow(new RuntimeException("Database connection failed"));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> createCustomer.execute(input));
        assertEquals("Database connection failed", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw NullPointerException for null input")
    void shouldThrowExceptionForNullInput() {
        // When & Then
        assertThrows(NullPointerException.class, () -> createCustomer.execute(null));
    }
}