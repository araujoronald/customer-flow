package com.github.araujoronald.application.useCases;

import com.github.araujoronald.application.ports.TicketRepository;
import com.github.araujoronald.application.ports.ViewQueue;
import com.github.araujoronald.domain.model.Ticket;
import com.github.araujoronald.domain.model.TicketStatus;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ViewQueueImpl implements ViewQueue {

    private final TicketRepository ticketRepository;

    public ViewQueueImpl(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    @Override
    public List<QueueTicket> execute(Input input) {
        Stream<Ticket> ticketStream = ticketRepository.findAll().stream()
                .filter(ticket -> ticket.getStatus() == TicketStatus.PENDING);

        if (input.customerId().isPresent()) {
            ticketStream = ticketStream.filter(ticket -> ticket.getCustomer().id().equals(input.customerId().get()));
        }

        if (input.qualifier().isPresent()) {
            ticketStream = ticketStream.filter(ticket -> ticket.getCustomer().qualifier() == input.qualifier().get());
        }

        return ticketStream
                .sorted(Comparator.comparing(Ticket::getPriority).reversed()
                        .thenComparing(Ticket::getCreated))
                .map(ticket -> new QueueTicket(ticket.getId(), ticket.getCustomer().name(), ticket.getPriority(), ticket.getStatus(), ticket.getCreated().toInstant()))
                .collect(Collectors.toList());
    }
}