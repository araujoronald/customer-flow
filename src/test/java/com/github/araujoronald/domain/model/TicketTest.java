package com.github.araujoronald.domain.model;

import com.github.araujoronald.ValidatorExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.text.MessageFormat;
import java.util.Date;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(ValidatorExtension.class)
class TicketTest {

    private User user;
    private Attendant attendant;

    @BeforeEach
    void setUp() {
        user = User.create("Test User", "user@test.com", "+1234567890", UserQualifier.DEFAULT);
        attendant = Attendant.create("Test Attendant", "attendant@test.com");
    }

    @Test
    @DisplayName("Should create a valid ticket with no constraint violations")
    void shouldCreateValidTicket(Validator validator) {
        // When
        Ticket ticket = Ticket.create(TicketStatus.PENDING, 10, user, attendant);
        Set<ConstraintViolation<Ticket>> violations = validator.validate(ticket);

        // Then
        assertTrue(violations.isEmpty());
        assertNotNull(ticket.id);
        assertEquals(TicketStatus.PENDING, ticket.status);
    }

    @Test
    @DisplayName("Should have violation for negative priority")
    void shouldHaveViolationForNegativePriority(Validator validator) {
        // When
        Ticket ticket = Ticket.create(TicketStatus.PENDING, -1, user, attendant);
        Set<ConstraintViolation<Ticket>> violations = validator.validate(ticket);

        // Then
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("priority", violations.iterator().next().getPropertyPath().toString());
    }

    @Test
    @DisplayName("Should have violations for null fields")
    void shouldHaveViolationsForNullFields(Validator validator) {
        // When
        Ticket ticket = Ticket.create(null, null, null, null);
        Set<ConstraintViolation<Ticket>> violations = validator.validate(ticket);

        // Then: Expecting violations for status, priority, user, attendant
        assertEquals(4, violations.size());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("status")));
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("priority")));
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("user")));
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("attendant")));
    }

    // --- State Transition Tests (Behavioral) ---

    @Test
    @DisplayName("Should throw exception when starting a non-pending ticket")
    void shouldThrowExceptionWhenStartingNonPendingTicket() {
        // Given
        Ticket inProgressTicket = Ticket.create(TicketStatus.PENDING, 5, user, attendant);
        inProgressTicket.start(); // now IN_PROGRESS

        Ticket completedTicket = Ticket.create(TicketStatus.PENDING, 5, user, attendant);
        completedTicket.start();
        completedTicket.complete(); // now COMPLETED

        Ticket cancelledTicket = Ticket.create(TicketStatus.PENDING, 5, user, attendant);
        cancelledTicket.cancel(); // now CANCELLED

        // Then
        var exInProgress = assertThrows(IllegalStateException.class, inProgressTicket::start);
        assertEquals("ticket.start.invalid.state", exInProgress.getMessage());
        var exCompleted = assertThrows(IllegalStateException.class, completedTicket::start);
        assertEquals("ticket.start.invalid.state", exCompleted.getMessage());
        var exCancelled = assertThrows(IllegalStateException.class, cancelledTicket::start);
        assertEquals("ticket.start.invalid.state", exCancelled.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when completing a ticket that is not in progress")
    void shouldThrowExceptionWhenCompletingNonInProgressTicket() {
        // Given
        Ticket pendingTicket = Ticket.create(TicketStatus.PENDING, 5, user, attendant);

        // Then
        var exception = assertThrows(IllegalStateException.class, pendingTicket::complete);
        assertEquals("ticket.complete.invalid.state", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when cancelling a completed ticket")
    void shouldThrowExceptionWhenCancellingCompletedTicket() {
        // Given
        Ticket completedTicket = Ticket.create(TicketStatus.PENDING, 5, user, attendant);
        completedTicket.start();
        completedTicket.complete();

        // Then
        var exception = assertThrows(IllegalStateException.class, completedTicket::cancel);
        String expectedMessage = MessageFormat.format("ticket.cancel.invalid.state", TicketStatus.COMPLETED);
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when cancelling an already cancelled ticket")
    void shouldThrowExceptionWhenCancellingCancelledTicket() {
        // Given
        Ticket cancelledTicket = Ticket.create(TicketStatus.PENDING, 5, user, attendant);
        cancelledTicket.cancel();

        // Then
        var exception = assertThrows(IllegalStateException.class, cancelledTicket::cancel);
        String expectedMessage = MessageFormat.format("ticket.cancel.invalid.state", TicketStatus.CANCELLED);
        assertEquals(expectedMessage, exception.getMessage());
    }
}