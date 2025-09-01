package com.github.araujoronald.infra.api.rest.springboot.dtos;

import java.util.UUID;

public record ReassignTicketRequest(UUID newAttendantId) {
}