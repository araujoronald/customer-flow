package com.github.araujoronald.infra.api.rest.springboot.dtos;

import java.util.Optional;
import java.util.UUID;

public record CompleteServiceRequest(UUID attendantId, Optional<String> description) {
}