package com.github.araujoronald.application.ports;

import java.util.UUID;

public interface ReassignTicket {

    Output execute(Input input);

    record Input(UUID ticketId, UUID newAttendantId) {}

    record Output(UUID ticketId) {}
}