package com.github.araujoronald.infra.api.rest.springboot.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.araujoronald.application.ports.CreateCustomer;
import com.github.araujoronald.application.ports.CustomerRepository;
import com.github.araujoronald.domain.model.Customer;
import com.github.araujoronald.domain.model.CustomerQualifier;
import com.github.araujoronald.infra.api.rest.springboot.dtos.UpdateCustomerRequest;
import com.github.araujoronald.infra.api.rest.springboot.exceptions.RestExceptionHandler;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CustomerController.class)
@Import(RestExceptionHandler.class) // Importa o handler para testar cenários de exceção
class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CustomerRepository customerRepository;

    @Autowired
    private MessageSource messageSource;

    @Test
    @DisplayName("POST /customers - Should create a customer and return 201 Created")
    void createCustomer_shouldReturnCreated() throws Exception {
        // Given
        var input = new CreateCustomer.Input(
                "John Doe",
                "john.doe@example.com",
                "+14155552671",
                CustomerQualifier.DEFAULT);
        var customerId = UUID.randomUUID();
        var savedCustomer = new Customer(
                customerId,
                input.name(),
                input.email(),
                input.phone(),
                input.qualifier());

        when(customerRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(customerRepository.save(any(Customer.class))).thenReturn(savedCustomer);

        // When & Then
        mockMvc.perform(post("/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input))
                        .header("Accept-Language", "en-US"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.customerId").value(customerId.toString()));
    }

    @Test
    @DisplayName("POST /customers - Should return 409 Conflict when customer email already exists")
    void createCustomer_whenEmailExists_shouldReturnConflict() throws Exception {
        // Given
        var input = new CreateCustomer.Input(
                "Jane Doe",
                "jane.doe@example.com",
                "+14155552672",
                CustomerQualifier.VIP);
        var existingCustomer = Customer.create(input.name(), input.email(), input.phone(), input.qualifier());

        when(customerRepository.findByEmail(input.email())).thenReturn(Optional.of(existingCustomer));

        String expectedMessage = messageSource.getMessage(
                "customer.already.exists", new Object[]{input.email()}, Locale.US);

        // When & Then
        mockMvc.perform(post("/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input))
                        .header("Accept-Language", "en-US"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error").value("Conflict"))
                .andExpect(jsonPath("$.message").value(expectedMessage));
    }

    @Test
    @DisplayName("PUT /customers/{id} - Should update a customer and return 200 OK")
    void updateCustomer_shouldReturnOk() throws Exception {
        // Given
        var customerId = UUID.randomUUID();
        var request = new UpdateCustomerRequest(
                "John Doe Updated",
                "john.doe.new@example.com",
                "+14155552674",
                CustomerQualifier.VIP);
        var existingCustomer = new Customer(customerId, "John Doe", "john.doe@example.com", "+14155552671", CustomerQualifier.DEFAULT);
        var updatedCustomer = new Customer(customerId, request.name(), request.email(), request.phone(), request.qualifier());

        when(customerRepository.find(customerId)).thenReturn(Optional.of(existingCustomer));
        when(customerRepository.save(any(Customer.class))).thenReturn(updatedCustomer);

        // When & Then
        mockMvc.perform(put("/customers/{id}", customerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Accept-Language", "en-US"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerId").value(customerId.toString()));
    }

    @Test
    @DisplayName("PUT /customers/{id} - Should return 404 Not Found when customer does not exist")
    void updateCustomer_whenNotFound_shouldReturnNotFound() throws Exception {
        // Given
        var customerId = UUID.randomUUID();
        var request = new UpdateCustomerRequest("John Doe Updated", "john.doe.new@example.com", "+14155552674", CustomerQualifier.VIP);

        when(customerRepository.find(customerId)).thenReturn(Optional.empty());

        String expectedMessage = messageSource.getMessage(
                "customer.not.found", new Object[]{customerId}, Locale.US);

        // When & Then
        mockMvc.perform(put("/customers/{id}", customerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Accept-Language", "en-US"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value(expectedMessage));
    }
}