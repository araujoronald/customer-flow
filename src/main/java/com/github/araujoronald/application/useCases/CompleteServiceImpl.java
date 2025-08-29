package com.github.araujoronald.application.useCases;

import com.github.araujoronald.application.exceptions.TicketAttendantMismatchException;
import com.github.araujoronald.application.exceptions.TicketNotFoundException;
import com.github.araujoronald.application.ports.CompleteService;
import com.github.araujoronald.application.ports.TicketRepository;
import com.github.araujoronald.domain.model.Ticket;

import java.text.MessageFormat;

public class CompleteServiceImpl implements CompleteService {

    private final TicketRepository ticketRepository;

    public CompleteServiceImpl(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    @Override
    public Output execute(Input input) {
        Ticket ticket = ticketRepository.find(input.ticketId())
                .orElseThrow(() -> new TicketNotFoundException(
                        MessageFormat.format("ticket.not.found", input.ticketId())
                ));

        if (!ticket.getAttendant().id().equals(input.attendantId())) {
            throw new TicketAttendantMismatchException(
                    MessageFormat.format("ticket.attendant.mismatch", input.attendantId())
            );
        }

        input.description().ifPresent(ticket::addDescription);

        ticket.complete();

        ticketRepository.save(ticket);

        return new Output(ticket.getId());
    }
}