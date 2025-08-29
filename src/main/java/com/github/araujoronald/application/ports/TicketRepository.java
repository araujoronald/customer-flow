package com.github.araujoronald.application.ports;

import com.github.araujoronald.domain.model.Ticket;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public interface TicketRepository {
    Ticket save(Ticket ticket);
    Optional<Ticket> find(UUID id);
    Collection<Ticket> findAll();
}