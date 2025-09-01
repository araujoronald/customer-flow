package com.github.araujoronald.application.useCases;

import com.github.araujoronald.application.exceptions.CustomerAlreadyExistsException;
import com.github.araujoronald.application.ports.CreateCustomer;
import com.github.araujoronald.application.ports.CustomerRepository;
import com.github.araujoronald.domain.model.Customer;

import java.text.MessageFormat;

public class CreateCustomerImpl implements CreateCustomer {

    private final CustomerRepository repository;

    public CreateCustomerImpl(CustomerRepository customerRepository){
        this.repository = customerRepository;
    }

    public Output execute(Input input){
        Customer customer = Customer.create(input.name(), input.email(), input.phone(), input.qualifier());

        repository.findByEmail(input.email()).ifPresent(c -> {
            throw new CustomerAlreadyExistsException("customer.already.exists", c.email());
        });

        final var customerSaved = this.repository.save(customer);
        return new Output(customerSaved.id());
    }

}
