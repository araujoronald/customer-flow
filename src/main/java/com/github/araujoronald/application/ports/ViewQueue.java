package com.github.araujoronald.application.ports;

import com.github.araujoronald.domain.model.CustomerQualifier;
import com.github.araujoronald.domain.model.TicketStatus;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ViewQueue {

    List<QueueTicket> execute(Input input);

    record Input(Optional<UUID> customerId, Optional<CustomerQualifier> qualifier) {}

    record QueueTicket(
            UUID ticketId,
            String customerName,
            Integer priority,
            TicketStatus status,
            Instant createdAt
    ) {}
}