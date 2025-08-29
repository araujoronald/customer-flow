package com.github.araujoronald.infra.adapters.repositories.memory;

import com.github.araujoronald.application.ports.TicketRepository;
import com.github.araujoronald.domain.model.Ticket;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class TicketRepositoryMemory implements TicketRepository {

    private final Map<UUID, Ticket> table = new ConcurrentHashMap<>();

    @Override
    public Ticket save(Ticket ticket) {
        table.put(ticket.getId(), ticket);
        return ticket;
    }

    @Override
    public Optional<Ticket> find(UUID id) {
        return Optional.ofNullable(table.get(id));
    }

    @Override
    public Collection<Ticket> findAll() {
        return table.values();
    }
}