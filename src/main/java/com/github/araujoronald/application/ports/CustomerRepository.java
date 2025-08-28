package com.github.araujoronald.application.ports;

import com.github.araujoronald.domain.model.Customer;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public interface CustomerRepository {

    Customer save(Customer customer);

    Optional<Customer> find(UUID id);

    Collection<Customer> findAll();
}
