package com.github.araujoronald.application.useCases;

import com.github.araujoronald.application.exceptions.CustomerNotFoundException;
import com.github.araujoronald.application.ports.CustomerRepository;
import com.github.araujoronald.application.ports.UpdateCustomer;
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

import java.text.MessageFormat;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdateCustomerImplTest {

    @Mock
    private CustomerRepository customerRepository;

    private UpdateCustomer updateCustomer;

    @Captor
    private ArgumentCaptor<Customer> customerArgumentCaptor;

    @BeforeEach
    void setUp() {
        updateCustomer = new UpdateCustomerImpl(customerRepository);
    }

    @Test
    @DisplayName("Should update a customer successfully")
    void shouldUpdateCustomerSuccessfully() {
        // Given
        UUID customerId = UUID.randomUUID();
        var input = new UpdateCustomer.Input(
                customerId,
                "Jane Doe Updated",
                "jane.doe.updated@example.com",
                "+15559998888",
                CustomerQualifier.VIP
        );

        Customer existingCustomer = new Customer(
                customerId, "Jane Doe", "jane.doe@example.com", "+15551112222", CustomerQualifier.DEFAULT
        );

        when(customerRepository.find(customerId)).thenReturn(Optional.of(existingCustomer));
        when(customerRepository.save(any(Customer.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        UpdateCustomer.Output output = updateCustomer.execute(input);

        // Then
        assertNotNull(output);
        assertEquals(customerId, output.customerId());

        verify(customerRepository).save(customerArgumentCaptor.capture());
        Customer capturedCustomer = customerArgumentCaptor.getValue();

        assertEquals(customerId, capturedCustomer.id());
        assertEquals("Jane Doe Updated", capturedCustomer.name());
        assertEquals("jane.doe.updated@example.com", capturedCustomer.email());
        assertEquals("+15559998888", capturedCustomer.phone());
        assertEquals(CustomerQualifier.VIP, capturedCustomer.qualifier());
    }

    @Test
    @DisplayName("Should throw CustomerNotFoundException when customer does not exist")
    void shouldThrowExceptionWhenCustomerNotFound() {
        // Given
        UUID nonExistentId = UUID.randomUUID();
        var input = new UpdateCustomer.Input(nonExistentId, "name", "email", "phone", CustomerQualifier.DEFAULT);

        when(customerRepository.find(nonExistentId)).thenReturn(Optional.empty());

        // When & Then
        CustomerNotFoundException exception = assertThrows(
                CustomerNotFoundException.class,
                () -> updateCustomer.execute(input)
        );

        String expectedMessage = MessageFormat.format("customer.not.found", nonExistentId);
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    @DisplayName("Should throw NullPointerException for null input")
    void shouldThrowExceptionForNullInput() {
        // When & Then
        assertThrows(NullPointerException.class, () -> updateCustomer.execute(null));
    }
}