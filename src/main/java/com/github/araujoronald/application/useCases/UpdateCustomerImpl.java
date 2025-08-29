package com.github.araujoronald.application.useCases;

import com.github.araujoronald.application.exceptions.CustomerNotFoundException;
import com.github.araujoronald.application.ports.CustomerRepository;
import com.github.araujoronald.application.ports.UpdateCustomer;
import com.github.araujoronald.domain.model.Customer;

import java.text.MessageFormat;

public class UpdateCustomerImpl implements UpdateCustomer {

    private final CustomerRepository repository;

    public UpdateCustomerImpl(CustomerRepository customerRepository) {
        this.repository = customerRepository;
    }

    @Override
    public Output execute(Input input) {
        Customer existingCustomer = repository.find(input.customerId())
                .orElseThrow(() -> {
                    String message = MessageFormat.format("customer.not.found", input.customerId());
                    return new CustomerNotFoundException(message);
                });

        Customer updatedCustomer = new Customer(
                existingCustomer.id(),
                input.name(),
                input.email(),
                input.phone(),
                input.qualifier()
        );

        final var customerSaved = this.repository.save(updatedCustomer);
        return new Output(customerSaved.id());
    }
}