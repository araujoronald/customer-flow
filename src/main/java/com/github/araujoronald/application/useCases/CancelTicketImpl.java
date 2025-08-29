package com.github.araujoronald.application.useCases;

import com.github.araujoronald.application.exceptions.TicketNotFoundException;
import com.github.araujoronald.application.ports.CancelTicket;
import com.github.araujoronald.application.ports.TicketRepository;
import com.github.araujoronald.domain.model.Ticket;

import java.text.MessageFormat;

public class CancelTicketImpl implements CancelTicket {

    private final TicketRepository ticketRepository;

    public CancelTicketImpl(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    @Override
    public Output execute(Input input) {
        Ticket ticket = ticketRepository.find(input.ticketId())
                .orElseThrow(() -> new TicketNotFoundException(
                        MessageFormat.format("ticket.not.found", input.ticketId())
                ));

        ticket.addCancellationReason(input.reason());
        ticket.cancel();

        ticketRepository.save(ticket);

        return new Output(ticket.getId());
    }
}