package com.github.araujoronald.application.useCases;

import com.github.araujoronald.application.exceptions.AttendantNotFoundException;
import com.github.araujoronald.application.exceptions.NoPendingTicketsException;
import com.github.araujoronald.application.ports.AttendantRepository;
import com.github.araujoronald.application.ports.PullNextTicket;
import com.github.araujoronald.application.ports.TicketRepository;
import com.github.araujoronald.domain.model.Attendant;
import com.github.araujoronald.domain.model.Ticket;
import com.github.araujoronald.domain.model.TicketStatus;

import java.text.MessageFormat;
import java.util.Comparator;

public class PullNextTicketImpl implements PullNextTicket {

    private final TicketRepository ticketRepository;
    private final AttendantRepository attendantRepository;

    public PullNextTicketImpl(TicketRepository ticketRepository, AttendantRepository attendantRepository) {
        this.ticketRepository = ticketRepository;
        this.attendantRepository = attendantRepository;
    }

    @Override
    public Output execute(Input input) {
        Attendant attendant = attendantRepository.find(input.attendantId())
                .orElseThrow(() -> new AttendantNotFoundException(MessageFormat.format("attendant.not.found", input.attendantId())));

        Ticket nextTicket = ticketRepository.findAll().stream()
                .filter(ticket -> ticket.getStatus() == TicketStatus.PENDING)
                .sorted(Comparator.comparing(Ticket::getPriority).reversed().thenComparing(Ticket::getCreated))
                .findFirst()
                .orElseThrow(() -> new NoPendingTicketsException("queue.no.pending.tickets"));

        nextTicket.assignTo(attendant);
        nextTicket.start();

        ticketRepository.save(nextTicket);

        return new Output(nextTicket.getId(), nextTicket.getCustomer().id(), nextTicket.getCustomer().name());
    }
}