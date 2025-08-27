package com.github.araujoronald.domain.model;

import javax.validation.constraints.*;
import java.util.UUID;

public record Customer(
        @NotNull UUID id,
        @NotBlank @Size(min = 3) String name,
        @Email String email,
        @NotBlank @InternationalPhone String phone,
        @NotNull CustomerQualifier qualifier) {

    public static Customer create(String name, String email, String phone, CustomerQualifier qualifier){
        return new Customer(UUID.randomUUID(), name, email, phone, qualifier);
    }
}
