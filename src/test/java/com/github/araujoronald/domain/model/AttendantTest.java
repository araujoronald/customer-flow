package com.github.araujoronald.domain.model;

import com.github.araujoronald.ValidatorExtension;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(ValidatorExtension.class)
class AttendantTest {

    @Test
    @DisplayName("Should create a valid attendant with no constraint violations")
    void shouldCreateValidAttendant(Validator validator) {
        // Given
        String name = "Jane Smith";
        String email = "jane.smith@example.com";

        // When
        Attendant attendant = Attendant.create(name, email);
        Set<ConstraintViolation<Attendant>> violations = validator.validate(attendant);

        // Then
        assertNotNull(attendant.id());
        assertEquals(name, attendant.name());
        assertEquals(email, attendant.email());
        assertTrue(violations.isEmpty(), "A valid attendant should have no constraint violations");
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "  ", "ab"})
    @DisplayName("Should have violations for invalid name")
    void shouldHaveViolationsForInvalidName(String invalidName, Validator validator) {
        // When
        Attendant attendant = Attendant.create(invalidName, "test@test.com");
        Set<ConstraintViolation<Attendant>> violations = validator.validate(attendant);

        // Then
        assertFalse(violations.isEmpty(), "An invalid name should produce violations");
        assertEquals(1, violations.size());
        assertEquals("name", violations.iterator().next().getPropertyPath().toString());
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "  ", "invalid-email"})
    @DisplayName("Should have violations for invalid email")
    void shouldHaveViolationsForInvalidEmail(String invalidEmail, Validator validator) {
        Attendant attendant = Attendant.create("Valid Name", invalidEmail);
        Set<ConstraintViolation<Attendant>> violations = validator.validate(attendant);
        assertFalse(violations.isEmpty());
        assertEquals("email", violations.iterator().next().getPropertyPath().toString());
    }
}