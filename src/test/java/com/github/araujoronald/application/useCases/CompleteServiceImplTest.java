package com.github.araujoronald.application.useCases;

import com.github.araujoronald.application.exceptions.TicketAttendantMismatchException;
import com.github.araujoronald.application.exceptions.TicketNotFoundException;
import com.github.araujoronald.application.ports.CompleteService;
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
class CompleteServiceImplTest {

    @Mock
    private TicketRepository ticketRepository;

    private CompleteService completeService;

    @Captor
    private ArgumentCaptor<Ticket> ticketArgumentCaptor;

    private Customer customer;
    private Attendant attendant;
    private Ticket inProgressTicket;

    @BeforeEach
    void setUp() {
        completeService = new CompleteServiceImpl(ticketRepository);

        customer = new Customer(UUID.randomUUID(), "Customer", "cust@test.com", "+1", CustomerQualifier.DEFAULT);
        attendant = new Attendant(UUID.randomUUID(), "Attendant", "att@test.com");

        inProgressTicket = Ticket.create(TicketStatus.PENDING, 1, customer, attendant);
        inProgressTicket.start(); // Move to IN_PROGRESS
    }

    @Test
    @DisplayName("Should complete a ticket successfully with a description")
    void shouldCompleteTicketSuccessfully() {
        // Given
        var input = new CompleteService.Input(inProgressTicket.getId(), attendant.id(), Optional.of("Service completed with success."));
        when(ticketRepository.find(inProgressTicket.getId())).thenReturn(Optional.of(inProgressTicket));

        // When
        CompleteService.Output output = completeService.execute(input);

        // Then
        assertNotNull(output);
        assertEquals(inProgressTicket.getId(), output.ticketId());

        verify(ticketRepository).save(ticketArgumentCaptor.capture());
        Ticket savedTicket = ticketArgumentCaptor.getValue();

        assertEquals(TicketStatus.COMPLETED, savedTicket.getStatus());
        assertNotNull(savedTicket.getEnd());
        assertEquals("Service completed with success.", savedTicket.getDescription());
    }

    @Test
    @DisplayName("Should throw IllegalStateException when trying to complete a non-IN_PROGRESS ticket")
    void shouldThrowWhenTicketIsNotInProgress() {
        // Given
        Ticket pendingTicket = Ticket.create(TicketStatus.PENDING, 1, customer, attendant);
        var input = new CompleteService.Input(pendingTicket.getId(), attendant.id(), Optional.empty());
        when(ticketRepository.find(pendingTicket.getId())).thenReturn(Optional.of(pendingTicket));

        // When & Then
        assertThrows(IllegalStateException.class, () -> completeService.execute(input));
    }

    @Test
    @DisplayName("Should throw TicketNotFoundException when ticket does not exist")
    void shouldThrowWhenTicketNotFound() {
        // Given
        UUID nonExistentId = UUID.randomUUID();
        var input = new CompleteService.Input(nonExistentId, attendant.id(), Optional.empty());
        when(ticketRepository.find(nonExistentId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(TicketNotFoundException.class, () -> completeService.execute(input));
    }

    @Test
    @DisplayName("Should throw TicketAttendantMismatchException when attendant is not assigned to the ticket")
    void shouldThrowWhenAttendantDoesNotMatch() {
        // Given
        Attendant anotherAttendant = new Attendant(UUID.randomUUID(), "Another Attendant", "another@test.com");
        var input = new CompleteService.Input(inProgressTicket.getId(), anotherAttendant.id(), Optional.empty());
        when(ticketRepository.find(inProgressTicket.getId())).thenReturn(Optional.of(inProgressTicket));

        // When & Then
        assertThrows(TicketAttendantMismatchException.class, () -> completeService.execute(input));
    }
}