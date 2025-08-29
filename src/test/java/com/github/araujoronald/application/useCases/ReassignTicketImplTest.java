package com.github.araujoronald.application.useCases;

import com.github.araujoronald.application.exceptions.AttendantNotFoundException;
import com.github.araujoronald.application.exceptions.TicketNotFoundException;
import com.github.araujoronald.application.ports.AttendantRepository;
import com.github.araujoronald.application.ports.ReassignTicket;
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
class ReassignTicketImplTest {

    @Mock
    private TicketRepository ticketRepository;
    @Mock
    private AttendantRepository attendantRepository;

    private ReassignTicket reassignTicket;

    @Captor
    private ArgumentCaptor<Ticket> ticketArgumentCaptor;

    private Customer customer;
    private Attendant originalAttendant;
    private Attendant newAttendant;
    private Ticket ticket;

    @BeforeEach
    void setUp() {
        reassignTicket = new ReassignTicketImpl(ticketRepository, attendantRepository);

        customer = new Customer(UUID.randomUUID(), "Customer", "cust@test.com", "+1", CustomerQualifier.DEFAULT);
        originalAttendant = new Attendant(UUID.randomUUID(), "Original Attendant", "orig@test.com");
        newAttendant = new Attendant(UUID.randomUUID(), "New Attendant", "new@test.com");
        ticket = Ticket.create(TicketStatus.PENDING, 1, customer, originalAttendant);
    }

    @Test
    @DisplayName("Should reassign a ticket to a new attendant successfully")
    void shouldReassignTicketSuccessfully() {
        // Given
        var input = new ReassignTicket.Input(ticket.getId(), newAttendant.id());

        when(ticketRepository.find(ticket.getId())).thenReturn(Optional.of(ticket));
        when(attendantRepository.find(newAttendant.id())).thenReturn(Optional.of(newAttendant));

        // When
        ReassignTicket.Output output = reassignTicket.execute(input);

        // Then
        assertNotNull(output);
        assertEquals(ticket.getId(), output.ticketId());

        verify(ticketRepository).save(ticketArgumentCaptor.capture());
        Ticket savedTicket = ticketArgumentCaptor.getValue();

        assertEquals(newAttendant, savedTicket.getAttendant());
    }

    @Test
    @DisplayName("Should throw TicketNotFoundException when ticket does not exist")
    void shouldThrowWhenTicketNotFound() {
        // Given
        UUID nonExistentId = UUID.randomUUID();
        var input = new ReassignTicket.Input(nonExistentId, newAttendant.id());
        when(ticketRepository.find(nonExistentId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(TicketNotFoundException.class, () -> reassignTicket.execute(input));
    }

    @Test
    @DisplayName("Should throw AttendantNotFoundException when new attendant does not exist")
    void shouldThrowWhenNewAttendantNotFound() {
        // Given
        UUID nonExistentAttendantId = UUID.randomUUID();
        var input = new ReassignTicket.Input(ticket.getId(), nonExistentAttendantId);

        when(ticketRepository.find(ticket.getId())).thenReturn(Optional.of(ticket));
        when(attendantRepository.find(nonExistentAttendantId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(AttendantNotFoundException.class, () -> reassignTicket.execute(input));
    }
}