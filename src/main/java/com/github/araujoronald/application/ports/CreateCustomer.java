package com.github.araujoronald.application.ports;

import com.github.araujoronald.domain.model.CustomerQualifier;

import java.util.UUID;

public interface CreateCustomer {

    public Output execute(Input input);

    public record Input(String name, String email, String phone, CustomerQualifier qualifier){}

    public record Output(UUID customerId){}
}


