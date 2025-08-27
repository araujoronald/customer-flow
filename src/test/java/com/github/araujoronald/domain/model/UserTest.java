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
class UserTest {

    private String name, email, phone;
    private UserQualifier qualifier;

    @BeforeEach
    void setUp(){
        name = "John Doe";
        email = "john.doe@example.com";
        phone = "+15551234567";
        qualifier = UserQualifier.VIP;
    }

    @Test
    @DisplayName("Should create a valid user with no constraint violations")
    void shouldCreateValidUser(Validator validator) {
        // When
        User user = User.create(name, email, phone, qualifier);
        Set<ConstraintViolation<User>> violations = validator.validate(user);

        // Then
        assertTrue(violations.isEmpty(), "A valid user should have no constraint violations");
        assertNotNull(user.id());
        assertEquals(name, user.name());
        assertEquals(email, user.email());
        assertEquals(phone, user.phone());
        assertEquals(qualifier, user.qualifier());
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "  ", "ab"})
    @DisplayName("Should have violations for invalid name")
    void shouldHaveViolationsForInvalidName(String invalidName, Validator validator) {
        User user = User.create(invalidName, email, phone, qualifier);
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
        assertEquals("name", violations.iterator().next().getPropertyPath().toString());
    }

    @ParameterizedTest
    @ValueSource(strings = {"invalid-email", "user@.com", "user@domain."})
    @DisplayName("Should have violations for invalid email format")
    void shouldHaveViolationsForInvalidEmail(String invalidEmail, Validator validator) {
        User user = User.create(name, invalidEmail, phone, qualifier);
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
        assertEquals("email", violations.iterator().next().getPropertyPath().toString());
    }

    @Test
    @DisplayName("Should have violation for null qualifier")
    void shouldHaveViolationForNullQualifier(Validator validator) {
        User user = User.create(name, email, phone, null);
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("qualifier", violations.iterator().next().getPropertyPath().toString());
    }
}