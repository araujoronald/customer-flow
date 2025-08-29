package com.github.araujoronald.application.ports;

import java.util.UUID;

public interface CancelTicket {

    Output execute(Input input);

    record Input(UUID ticketId, String reason) {}

    record Output(UUID ticketId) {}
}