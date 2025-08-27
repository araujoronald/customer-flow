package com.github.araujoronald.domain.model;

import javax.validation.constraints.*;
import java.util.Objects;
import java.util.UUID;

public record User(
        @NotNull UUID id,
        @NotBlank @Size(min = 3) String name,
        @Email String email,
        @NotBlank @InternationalPhone String phone,
        @NotNull UserQualifier qualifier) {

    public static User create(String name, String email, String phone, UserQualifier qualifier){
        return new User(UUID.randomUUID(), name, email, phone, qualifier);
    }
}
