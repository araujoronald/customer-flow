package com.github.araujoronald.application.ports;

import java.util.UUID;

public interface CreateAttendant {
    Output execute(Input input);

    record Input(String name, String email) {}

    record Output(UUID attendantId) {}
}