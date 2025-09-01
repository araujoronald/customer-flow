package com.github.araujoronald.application.useCases;

import com.github.araujoronald.application.exceptions.TicketInvalidStateException;
import com.github.araujoronald.application.exceptions.TicketNotFoundException;
import com.github.araujoronald.application.ports.CancelTicket;
import com.github.araujoronald.application.ports.TicketRepository;
import com.github.araujoronald.domain.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CancelTicketImplTest {

    @Mock
    private TicketRepository ticketRepository;

    private CancelTicket cancelTicket;

    @Captor
    private ArgumentCaptor<Ticket> ticketArgumentCaptor;

    private Customer customer;
    private Attendant attendant;

    @BeforeEach
    void setUp() {
        cancelTicket = new CancelTicketImpl(ticketRepository);
        customer = new Customer(UUID.randomUUID(), "Customer", "cust@test.com", "+1", CustomerQualifier.DEFAULT);
        attendant = new Attendant(UUID.randomUUID(), "Attendant", "att@test.com");
    }

    @Test
    @DisplayName("Should cancel a PENDING ticket successfully")
    void shouldCancelPendingTicket() {
        // Given
        Ticket pendingTicket = Ticket.create(TicketStatus.PENDING, 1, customer, attendant);
        var input = new CancelTicket.Input(pendingTicket.getId(), "Customer requested cancellation.");
        when(ticketRepository.find(pendingTicket.getId())).thenReturn(Optional.of(pendingTicket));

        // When
        CancelTicket.Output output = cancelTicket.execute(input);

        // Then
        assertNotNull(output);
        assertEquals(pendingTicket.getId(), output.ticketId());

        verify(ticketRepository).save(ticketArgumentCaptor.capture());
        Ticket savedTicket = ticketArgumentCaptor.getValue();

        assertEquals(TicketStatus.CANCELLED, savedTicket.getStatus());
        assertEquals("Customer requested cancellation.", savedTicket.getCancellationReason());
    }

    @Test
    @DisplayName("Should cancel an IN_PROGRESS ticket successfully")
    void shouldCancelInProgressTicket() {
        // Given
        Ticket inProgressTicket = Ticket.create(TicketStatus.PENDING, 1, customer, attendant);
        inProgressTicket.start(); // Move to IN_PROGRESS
        var input = new CancelTicket.Input(inProgressTicket.getId(), "System error.");
        when(ticketRepository.find(inProgressTicket.getId())).thenReturn(Optional.of(inProgressTicket));

        // When
        cancelTicket.execute(input);

        // Then
        verify(ticketRepository).save(ticketArgumentCaptor.capture());
        Ticket savedTicket = ticketArgumentCaptor.getValue();

        assertEquals(TicketStatus.CANCELLED, savedTicket.getStatus());
        assertEquals("System error.", savedTicket.getCancellationReason());
    }

    @Test
    @DisplayName("Should throw IllegalStateException when trying to cancel a COMPLETED ticket")
    void shouldThrowWhenTicketIsCompleted() {
        // Given
        Ticket completedTicket = Ticket.create(TicketStatus.PENDING, 1, customer, attendant);
        completedTicket.start();
        completedTicket.complete(); // Move to COMPLETED

        var input = new CancelTicket.Input(completedTicket.getId(), "Too late.");
        when(ticketRepository.find(completedTicket.getId())).thenReturn(Optional.of(completedTicket));

        // When & Then
        assertThrows(TicketInvalidStateException.class, () -> cancelTicket.execute(input));
    }

    @Test
    @DisplayName("Should throw TicketNotFoundException when ticket does not exist")
    void shouldThrowWhenTicketNotFound() {
        // Given
        UUID nonExistentId = UUID.randomUUID();
        var input = new CancelTicket.Input(nonExistentId, "Doesn't matter.");
        when(ticketRepository.find(nonExistentId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(TicketNotFoundException.class, () -> cancelTicket.execute(input));
    }
}