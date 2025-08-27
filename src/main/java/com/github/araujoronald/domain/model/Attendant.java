package com.github.araujoronald.domain.model;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Objects;
import java.util.UUID;

public record Attendant(
        @NotNull UUID id,
        @Size(min = 3) String name,
        @NotBlank @Email String email) {

    public static Attendant create(String name, String email){
        return new Attendant(UUID.randomUUID(), name, email);
    }
}
