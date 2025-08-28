package com.github.araujoronald.infra.adapters.repositories;

import com.github.araujoronald.application.ports.CustomerRepository;
import com.github.araujoronald.domain.model.Customer;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class CustomerRepositoryMemory implements CustomerRepository {

    private final Map<UUID, Customer> table;

    public CustomerRepositoryMemory(){
        this.table = new ConcurrentHashMap<>();
    }

    public Customer save(Customer customer) {
        this.table.put(customer.id(), customer);
        return customer;

    }

    public Optional<Customer> find(UUID id) {
        return Optional.ofNullable(this.table.get(id));
    }

    public Collection<Customer> findAll() {
        return this.table.values();
    }
}
