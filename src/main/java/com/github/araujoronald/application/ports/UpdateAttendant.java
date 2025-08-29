package com.github.araujoronald.application.ports;

import java.util.UUID;

public interface UpdateAttendant {
    Output execute(Input input);

    record Input(UUID attendantId, String name, String email) {}

    record Output(UUID attendantId) {}
}