package com.github.araujoronald.application.useCases;

import com.github.araujoronald.application.exceptions.AttendantNotFoundException;
import com.github.araujoronald.application.exceptions.CustomerNotFoundException;
import com.github.araujoronald.application.ports.AttendantRepository;
import com.github.araujoronald.application.ports.CreateTicket;
import com.github.araujoronald.application.ports.CustomerRepository;
import com.github.araujoronald.application.ports.TicketRepository;
import com.github.araujoronald.domain.model.Attendant;
import com.github.araujoronald.domain.model.Customer;
import com.github.araujoronald.domain.model.Ticket;
import com.github.araujoronald.domain.model.TicketStatus;

import java.text.MessageFormat;

public class CreateTicketImpl implements CreateTicket {

    private final TicketRepository ticketRepository;
    private final CustomerRepository customerRepository;
    private final AttendantRepository attendantRepository;

    public CreateTicketImpl(
            TicketRepository ticketRepository,
            CustomerRepository customerRepository,
            AttendantRepository attendantRepository) {
        this.ticketRepository = ticketRepository;
        this.customerRepository = customerRepository;
        this.attendantRepository = attendantRepository;
    }

    @Override
    public Output execute(Input input) {
        Customer customer = customerRepository.find(input.customerId())
                .orElseThrow(() -> new CustomerNotFoundException(MessageFormat.format("customer.not.found", input.customerId())));

        Attendant attendant = attendantRepository.find(input.attendantId())
                .orElseThrow(() -> new AttendantNotFoundException(MessageFormat.format("attendant.not.found", input.attendantId())));

        int priority = customer.qualifier().getPriority();

        Ticket newTicket = Ticket.create(TicketStatus.PENDING, priority, customer, attendant);

        Ticket savedTicket = ticketRepository.save(newTicket);

        return new Output(savedTicket.getId());
    }
}