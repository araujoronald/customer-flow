package com.github.araujoronald.application.ports;

import java.util.UUID;

public interface PullNextTicket {

    Output execute(Input input);

    record Input(UUID attendantId) {}

    record Output(UUID ticketId, UUID customerId, String customerName) {}

}