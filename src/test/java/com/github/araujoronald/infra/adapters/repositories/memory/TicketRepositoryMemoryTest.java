package com.github.araujoronald.infra.adapters.repositories.memory;

import com.github.araujoronald.application.ports.TicketRepository;
import com.github.araujoronald.domain.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class TicketRepositoryMemoryTest {

    private TicketRepository ticketRepository;
    private Customer customer;
    private Attendant attendant;

    @BeforeEach
    void setUp() {
        ticketRepository = new TicketRepositoryMemory();
        customer = Customer.create("Test Customer", "cust@test.com", "+1", CustomerQualifier.DEFAULT);
        attendant = Attendant.create("Test Attendant", "att@test.com");
    }

    @Test
    @DisplayName("Should save a ticket and find it by ID")
    void shouldSaveAndFindTicketById() {
        // Given
        Ticket ticket = Ticket.create(TicketStatus.PENDING, 1, customer, attendant);

        // When
        ticketRepository.save(ticket);
        Optional<Ticket> foundTicketOpt = ticketRepository.find(ticket.getId());

        // Then
        assertTrue(foundTicketOpt.isPresent());
        assertEquals(ticket.getId(), foundTicketOpt.get().getId());
    }
}