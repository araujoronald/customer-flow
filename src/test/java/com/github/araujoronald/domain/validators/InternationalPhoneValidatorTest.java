package com.github.araujoronald.domain.validators;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InternationalPhoneValidatorTest {

    private InternationalPhoneValidator validator;

    @BeforeEach
    void setUp() {
        validator = new InternationalPhoneValidator();
    }

    @ParameterizedTest
    @ValueSource(strings = {"+5511987654321", "+14155552671", "+442071234567", "+919876543210", "+1987654321"})
    @DisplayName("Should return true for valid international phone numbers")
    void shouldReturnTrueForValidInternationalPhoneNumbers(String validPhone) {
        assertTrue(validator.isValid(validPhone, null));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "5511987654321",      // Missing '+'
            "+1-415-555-2671",    // Contains hyphens
            "+44 20 7123 4567",   // Contains spaces
            "+91(987)6543210",    // Contains parentheses
            "invalid-phone",      // Contains letters
            "+",                  // Too short
            "+12345678901234567"  // Too long (more than 15 digits)
    })
    @DisplayName("Should return false for invalid international phone numbers")
    void shouldReturnFalseForInvalidInternationalPhoneNumbers(String invalidPhone) {
        assertFalse(validator.isValid(invalidPhone, null));
    }

    @Test
    @DisplayName("Should return true for null, empty or blank strings")
    void shouldReturnTrueForNullOrBlankStrings() {
        assertTrue(validator.isValid(null, null), "Null should be considered valid by this validator");
        assertTrue(validator.isValid("", null), "Empty string should be considered valid");
        assertTrue(validator.isValid("   ", null), "Blank string should be considered valid");
    }
}