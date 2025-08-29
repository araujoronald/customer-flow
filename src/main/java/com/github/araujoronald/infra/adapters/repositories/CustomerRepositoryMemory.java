package com.github.araujoronald.infra.adapters.repositories;

import com.github.araujoronald.application.ports.CustomerRepository;
import com.github.araujoronald.domain.model.Customer;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class CustomerRepositoryMemory implements CustomerRepository {

    private final Map<UUID, Customer> table;
    private final Map<String, Customer> tableEmailIndex;

    public CustomerRepositoryMemory(){
        this.table = new ConcurrentHashMap<>();
        this.tableEmailIndex = new ConcurrentHashMap<>();
    }

    public Customer save(Customer customer) {
        this.table.put(customer.id(), customer);
        this.tableEmailIndex.put(customer.email(), customer);
        return customer;

    }

    public Optional<Customer> find(UUID id) {
        return Optional.ofNullable(this.table.get(id));
    }

    public Optional<Customer> findByEmail(String email) {
        return Optional.ofNullable(this.tableEmailIndex.get(email));
    }

    public Collection<Customer> findAll() {
        return this.table.values();
    }
}
