package com.github.araujoronald.application.useCases;

import com.github.araujoronald.application.exceptions.AttendantNotFoundException;
import com.github.araujoronald.application.exceptions.NoPendingTicketsException;
import com.github.araujoronald.application.ports.AttendantRepository;
import com.github.araujoronald.application.ports.PullNextTicket;
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

import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PullNextTicketImplTest {

    @Mock
    private TicketRepository ticketRepository;
    @Mock
    private AttendantRepository attendantRepository;

    private PullNextTicket pullNextTicket;

    @Captor
    private ArgumentCaptor<Ticket> ticketArgumentCaptor;

    private Attendant availableAttendant;

    @BeforeEach
    void setUp() {
        pullNextTicket = new PullNextTicketImpl(ticketRepository, attendantRepository);
        availableAttendant = new Attendant(UUID.randomUUID(), "Available Attendant", "att@test.com");
    }

    @Test
    @DisplayName("Should pull the highest priority ticket, assign it, start it, and save it")
    void shouldPullHighestPriorityTicket() {
        // Given
        var input = new PullNextTicket.Input(availableAttendant.id());

        Customer customerVip = new Customer(UUID.randomUUID(), "VIP User", "vip@test.com", "+1", CustomerQualifier.VIP);
        Customer customerDefault = new Customer(UUID.randomUUID(), "Default User", "def@test.com", "+2", CustomerQualifier.DEFAULT);

        Ticket vipTicket = Ticket.create(TicketStatus.PENDING, customerVip.qualifier().getPriority(), customerVip, null);
        Ticket defaultTicket = Ticket.create(TicketStatus.PENDING, customerDefault.qualifier().getPriority(), customerDefault, null);

        when(attendantRepository.find(availableAttendant.id())).thenReturn(Optional.of(availableAttendant));
        when(ticketRepository.findAll()).thenReturn(List.of(defaultTicket, vipTicket)); // Unordered

        // When
        PullNextTicket.Output output = pullNextTicket.execute(input);

        // Then
        assertNotNull(output);
        assertEquals(vipTicket.getId(), output.ticketId());
        assertEquals(customerVip.id(), output.customerId());

        verify(ticketRepository).save(ticketArgumentCaptor.capture());
        Ticket savedTicket = ticketArgumentCaptor.getValue();

        assertEquals(vipTicket.getId(), savedTicket.getId());
        assertEquals(TicketStatus.IN_PROGRESS, savedTicket.getStatus());
        assertEquals(availableAttendant, savedTicket.getAttendant());
        assertNotNull(savedTicket.getStart());
    }

    @Test
    @DisplayName("Should throw NoPendingTicketsException when queue is empty")
    void shouldThrowWhenQueueIsEmpty() {
        // Given
        var input = new PullNextTicket.Input(availableAttendant.id());

        when(attendantRepository.find(availableAttendant.id())).thenReturn(Optional.of(availableAttendant));
        when(ticketRepository.findAll()).thenReturn(List.of()); // No tickets

        // When & Then
        NoPendingTicketsException exception = assertThrows(
                NoPendingTicketsException.class,
                () -> pullNextTicket.execute(input)
        );
        assertEquals("queue.no.pending.tickets", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw AttendantNotFoundException when attendant does not exist")
    void shouldThrowWhenAttendantNotFound() {
        // Given
        UUID nonExistentAttendantId = UUID.randomUUID();
        var input = new PullNextTicket.Input(nonExistentAttendantId);

        when(attendantRepository.find(nonExistentAttendantId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(AttendantNotFoundException.class, () -> pullNextTicket.execute(input));
        verify(ticketRepository, never()).findAll();
        verify(ticketRepository, never()).save(any());
    }
}