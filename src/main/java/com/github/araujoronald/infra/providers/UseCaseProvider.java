package com.github.araujoronald.infra.providers;

import com.github.araujoronald.application.ports.CreateCustomer;
import com.github.araujoronald.application.ports.CustomerRepository;
import com.github.araujoronald.application.useCases.CreateCustomerImpl;

public class UseCaseProvider {

    private static CreateCustomer createCustomer;

    public static void registerCreateCustomer(CreateCustomer implementation){
        createCustomer = implementation;
    }

    public static CreateCustomer getCreateCustomer(CustomerRepository customerRepository){
        if(createCustomer == null)
            createCustomer = new CreateCustomerImpl(customerRepository);
        return createCustomer;
    }
}
