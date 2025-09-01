package com.github.araujoronald.infra.api.rest.springboot.dtos;

import com.github.araujoronald.domain.model.CustomerQualifier;

public record UpdateCustomerRequest(String name, String email, String phone, CustomerQualifier qualifier) {
}