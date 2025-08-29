package com.github.araujoronald.application.ports;

import com.github.araujoronald.domain.model.CustomerQualifier;

import java.util.UUID;

public interface UpdateCustomer {

    Output execute(Input input);

    record Input(UUID customerId, String name, String email, String phone, CustomerQualifier qualifier) {}

    record Output(UUID customerId) {}
}