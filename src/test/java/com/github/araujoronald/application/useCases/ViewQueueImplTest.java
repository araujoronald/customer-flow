package com.github.araujoronald.application.useCases;

import com.github.araujoronald.application.ports.TicketRepository;
import com.github.araujoronald.application.ports.ViewQueue;
import com.github.araujoronald.domain.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ViewQueueImplTest {

    @Mock
    private TicketRepository ticketRepository;

    private ViewQueue viewQueue;

    private Customer customerDefault;
    private Customer customerVip;
    private Customer customerExpress;
    private Attendant attendant;

    private Ticket ticket1_default_pending_old;
    private Ticket ticket2_vip_pending_new;
    private Ticket ticket3_express_pending_middle;
    private Ticket ticket4_vip_inprogress;
    private Ticket ticket5_default_pending_newest;

    @BeforeEach
    void setUp() throws Exception {
        viewQueue = new ViewQueueImpl(ticketRepository);

        // Setup entities
        customerDefault = new Customer(UUID.randomUUID(), "Default User", "default@test.com", "+1", CustomerQualifier.DEFAULT);
        customerVip = new Customer(UUID.randomUUID(), "VIP User", "vip@test.com", "+2", CustomerQualifier.VIP);
        customerExpress = new Customer(UUID.randomUUID(), "Express User", "express@test.com", "+3", CustomerQualifier.EXPRESS);
        attendant = new Attendant(UUID.randomUUID(), "Attendant", "att@test.com");

        // Setup tickets with varying properties
        Instant now = Instant.now();
        ticket1_default_pending_old = createTestTicket(TicketStatus.PENDING, customerDefault, attendant, Date.from(now.minusSeconds(30)));
        ticket2_vip_pending_new = createTestTicket(TicketStatus.PENDING, customerVip, attendant, Date.from(now.minusSeconds(10)));
        ticket3_express_pending_middle = createTestTicket(TicketStatus.PENDING, customerExpress, attendant, Date.from(now.minusSeconds(20)));
        ticket4_vip_inprogress = createTestTicket(TicketStatus.IN_PROGRESS, customerVip, attendant, Date.from(now.minusSeconds(40)));
        ticket5_default_pending_newest = createTestTicket(TicketStatus.PENDING, customerDefault, attendant, Date.from(now));
    }

    @Test
    @DisplayName("Should return all PENDING tickets sorted by priority (desc) and created (asc) when no filter is provided")
    void shouldReturnAllPendingTicketsSorted() {
        // Given
        when(ticketRepository.findAll()).thenReturn(List.of(
                ticket1_default_pending_old,
                ticket2_vip_pending_new,
                ticket3_express_pending_middle,
                ticket4_vip_inprogress, // Should be filtered out
                ticket5_default_pending_newest
        ));
        var input = new ViewQueue.Input(Optional.empty(), Optional.empty());

        // When
        List<ViewQueue.QueueTicket> queue = viewQueue.execute(input);

        // Then
        // Expected order: Express (5), VIP (3), Default (1, older), Default (1, newer)
        assertEquals(4, queue.size());
        assertEquals(ticket3_express_pending_middle.getId(), queue.get(0).ticketId());
        assertEquals(ticket2_vip_pending_new.getId(), queue.get(1).ticketId());
        assertEquals(ticket1_default_pending_old.getId(), queue.get(2).ticketId());
        assertEquals(ticket5_default_pending_newest.getId(), queue.get(3).ticketId());
    }

    @Test
    @DisplayName("Should return only VIP tickets when filtered by VIP qualifier")
    void shouldReturnFilteredByQualifier() {
        // Given
        when(ticketRepository.findAll()).thenReturn(List.of(ticket1_default_pending_old, ticket2_vip_pending_new, ticket3_express_pending_middle));
        var input = new ViewQueue.Input(Optional.empty(), Optional.of(CustomerQualifier.VIP));

        // When
        List<ViewQueue.QueueTicket> queue = viewQueue.execute(input);

        // Then
        assertEquals(1, queue.size());
        assertEquals(ticket2_vip_pending_new.getId(), queue.get(0).ticketId());
    }

    @Test
    @DisplayName("Should return only tickets for a specific customer when filtered by customerId")
    void shouldReturnFilteredByCustomerId() {
        // Given
        when(ticketRepository.findAll()).thenReturn(List.of(ticket1_default_pending_old, ticket2_vip_pending_new, ticket5_default_pending_newest));
        var input = new ViewQueue.Input(Optional.of(customerDefault.id()), Optional.empty());

        // When
        List<ViewQueue.QueueTicket> queue = viewQueue.execute(input);

        // Then
        assertEquals(2, queue.size());
        assertEquals(ticket1_default_pending_old.getId(), queue.get(0).ticketId()); // Older first
        assertEquals(ticket5_default_pending_newest.getId(), queue.get(1).ticketId());
    }

    /**
     * Test helper to create a Ticket instance and set its creation date via reflection.
     * This is a workaround because the domain model is not designed for easy testing.
     */
    private Ticket createTestTicket(TicketStatus status, Customer customer, Attendant attendant, Date creationDate) throws Exception {
        Ticket ticket = Ticket.create(status, customer.qualifier().getPriority(), customer, attendant);
        Field createdField = Ticket.class.getDeclaredField("created");
        createdField.setAccessible(true);
        createdField.set(ticket, creationDate);
        return ticket;
    }
}