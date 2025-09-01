package com.github.araujoronald.infra.api.rest.springboot.controllers;

import com.github.araujoronald.application.ports.CreateCustomer;
import com.github.araujoronald.application.ports.CustomerRepository;
import com.github.araujoronald.application.ports.UpdateCustomer;
import com.github.araujoronald.infra.api.rest.springboot.dtos.UpdateCustomerRequest;
import com.github.araujoronald.infra.providers.UseCaseProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.UUID;

@RestController
@RequestMapping("/customers")
public class CustomerController {

    private final CustomerRepository customerRepository;

    @Autowired
    public CustomerController(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @PostMapping
    public ResponseEntity<CreateCustomer.Output> createCustomer(@Valid @RequestBody CreateCustomer.Input input) {
        CreateCustomer useCase = UseCaseProvider.getCreateCustomer(customerRepository);
        CreateCustomer.Output output = useCase.execute(input);
        return ResponseEntity.status(HttpStatus.CREATED).body(output);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UpdateCustomer.Output> updateCustomer(@PathVariable UUID id, @RequestBody UpdateCustomerRequest request) {
        UpdateCustomer useCase = UseCaseProvider.getUpdateCustomer(customerRepository);
        UpdateCustomer.Input input = new UpdateCustomer.Input(id, request.name(), request.email(), request.phone(), request.qualifier());
        UpdateCustomer.Output output = useCase.execute(input);
        return ResponseEntity.ok(output);
    }
}