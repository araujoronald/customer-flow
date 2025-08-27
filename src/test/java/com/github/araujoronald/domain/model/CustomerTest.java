package com.github.araujoronald.domain.model;

import com.github.araujoronald.ValidatorExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.Set;

@ExtendWith(ValidatorExtension.class)
class CustomerTest {

    private String name, email, phone;
    private CustomerQualifier qualifier;

    @BeforeEach
    void setUp(){
        name = "John Doe";
        email = "john.doe@example.com";
        phone = "+15551234567";
        qualifier = CustomerQualifier.VIP;
    }

    @Test
    @DisplayName("Should create a valid user with no constraint violations")
    void shouldCreateValidUser(Validator validator) {
        // When
        Customer customer = Customer.create(name, email, phone, qualifier);
        Set<ConstraintViolation<Customer>> violations = validator.validate(customer);

        // Then
        assertTrue(violations.isEmpty(), "A valid user should have no constraint violations");
        assertNotNull(customer.id());
        assertEquals(name, customer.name());
        assertEquals(email, customer.email());
        assertEquals(phone, customer.phone());
        assertEquals(qualifier, customer.qualifier());
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "  ", "ab"})
    @DisplayName("Should have violations for invalid name")
    void shouldHaveViolationsForInvalidName(String invalidName, Validator validator) {
        Customer customer = Customer.create(invalidName, email, phone, qualifier);
        Set<ConstraintViolation<Customer>> violations = validator.validate(customer);
        assertFalse(violations.isEmpty());
        assertEquals("name", violations.iterator().next().getPropertyPath().toString());
    }

    @ParameterizedTest
    @ValueSource(strings = {"invalid-email", "user@.com", "user@domain."})
    @DisplayName("Should have violations for invalid email format")
    void shouldHaveViolationsForInvalidEmail(String invalidEmail, Validator validator) {
        Customer customer = Customer.create(name, invalidEmail, phone, qualifier);
        Set<ConstraintViolation<Customer>> violations = validator.validate(customer);
        assertFalse(violations.isEmpty());
        assertEquals("email", violations.iterator().next().getPropertyPath().toString());
    }

    @Test
    @DisplayName("Should have violation for null qualifier")
    void shouldHaveViolationForNullQualifier(Validator validator) {
        Customer customer = Customer.create(name, email, phone, null);
        Set<ConstraintViolation<Customer>> violations = validator.validate(customer);
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("qualifier", violations.iterator().next().getPropertyPath().toString());
    }
}