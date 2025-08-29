package com.github.araujoronald.application.ports;

import java.util.UUID;

public interface CreateTicket {
    Output execute(Input input);

    record Input(UUID customerId, UUID attendantId) {}

    record Output(UUID ticketId) {}
}