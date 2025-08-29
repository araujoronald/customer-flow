package com.github.araujoronald.application.useCases;

import com.github.araujoronald.application.exceptions.InvalidEscalationInputException;
import com.github.araujoronald.application.ports.EscalateTicket;
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

import java.lang.reflect.Field;
import java.time.Instant;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EscalateTicketImplTest {

    @Mock
    private TicketRepository ticketRepository;

    private EscalateTicket escalateTicket;

    @Captor
    private ArgumentCaptor<Ticket> ticketArgumentCaptor;

    private Customer customerDefault;
    private Customer customerVip;
    private Attendant attendant;

    @BeforeEach
    void setUp() {
        escalateTicket = new EscalateTicketImpl(ticketRepository);

        customerDefault = new Customer(null, "Default User", "default@test.com", "+1", CustomerQualifier.DEFAULT); // Priority 1
        customerVip = new Customer(null, "VIP User", "vip@test.com", "+2", CustomerQualifier.VIP); // Priority 3
        attendant = new Attendant(null, "Attendant", "att@test.com");
    }

    @Test
    @DisplayName("Should escalate ticket priority based on elapsed time windows")
    void shouldEscalateTicketPriority() throws Exception {
        // Given: A ticket created 25 minutes ago
        Ticket ticket = createTestTicket(customerDefault, Date.from(Instant.now().minusSeconds(25 * 60)));
        assertEquals(1, ticket.getPriority());

        when(ticketRepository.findAll()).thenReturn(List.of(ticket));

        // When: Escalation runs with a 10-minute window and a gain of 2
        var input = new EscalateTicket.Input(2, 10);
        EscalateTicket.Output output = escalateTicket.execute(input);

        // Then: The ticket priority should be base (1) + 2 windows * 2 gain = 5
        assertEquals(1, output.escalatedTicketsCount());
        verify(ticketRepository).save(ticketArgumentCaptor.capture());
        assertEquals(5, ticketArgumentCaptor.getValue().getPriority());
    }

    @Test
    @DisplayName("Should not escalate ticket if not enough time has passed")
    void shouldNotEscalateTicket() throws Exception {
        // Given: A ticket created 5 minutes ago
        Ticket ticket = createTestTicket(customerDefault, Date.from(Instant.now().minusSeconds(5 * 60)));
        when(ticketRepository.findAll()).thenReturn(List.of(ticket));

        // When: Escalation runs with a 10-minute window
        var input = new EscalateTicket.Input(1, 10);
        EscalateTicket.Output output = escalateTicket.execute(input);

        // Then: No ticket should be escalated
        assertEquals(0, output.escalatedTicketsCount());
        verify(ticketRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should only escalate PENDING tickets")
    void shouldOnlyEscalatePendingTickets() throws Exception {
        // Given
        Ticket pendingTicket = createTestTicket(customerDefault, Date.from(Instant.now().minusSeconds(15 * 60)));
        Ticket inProgressTicket = createTestTicket(customerVip, Date.from(Instant.now().minusSeconds(15 * 60)));
        inProgressTicket.start();

        when(ticketRepository.findAll()).thenReturn(List.of(pendingTicket, inProgressTicket));

        // When
        var input = new EscalateTicket.Input(1, 10);
        EscalateTicket.Output output = escalateTicket.execute(input);

        // Then: Only the pending ticket should be saved
        assertEquals(1, output.escalatedTicketsCount());
        verify(ticketRepository, times(1)).save(pendingTicket);
        verify(ticketRepository, never()).save(inProgressTicket);
    }

    @Test
    @DisplayName("Should throw InvalidEscalationInputException for invalid scale gain")
    void shouldThrowForInvalidScaleGain() {
        var input = new EscalateTicket.Input(4, 10); // Max is 3
        assertThrows(InvalidEscalationInputException.class, () -> escalateTicket.execute(input));

        var input2 = new EscalateTicket.Input(0, 10); // Min is 1
        assertThrows(InvalidEscalationInputException.class, () -> escalateTicket.execute(input2));
    }

    private Ticket createTestTicket(Customer customer, Date creationDate) throws Exception {
        Ticket ticket = Ticket.create(TicketStatus.PENDING, customer.qualifier().getPriority(), customer, attendant);
        Field createdField = Ticket.class.getDeclaredField("created");
        createdField.setAccessible(true);
        createdField.set(ticket, creationDate);
        return ticket;
    }
}