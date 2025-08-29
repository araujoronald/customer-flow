package com.github.araujoronald.application.ports;

import java.util.Optional;
import java.util.UUID;

public interface CompleteService {

    Output execute(Input input);

    record Input(UUID ticketId, UUID attendantId, Optional<String> description) {}

    record Output(UUID ticketId) {}
}