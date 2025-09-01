package com.github.araujoronald.application.ports;

import com.github.araujoronald.domain.model.CustomerQualifier;
import com.github.araujoronald.domain.model.InternationalPhone;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.UUID;

public interface CreateCustomer {

    public Output execute(Input input);

    public record Input(@NotBlank @Size(min = 3) String name, @NotBlank @Email String email,
            @NotBlank @InternationalPhone String phone, @NotNull CustomerQualifier qualifier) {
    }

    public record Output(UUID customerId){}
}
