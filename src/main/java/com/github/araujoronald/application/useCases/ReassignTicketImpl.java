package com.github.araujoronald.application.useCases;

import com.github.araujoronald.application.exceptions.AttendantNotFoundException;
import com.github.araujoronald.application.exceptions.TicketNotFoundException;
import com.github.araujoronald.application.ports.AttendantRepository;
import com.github.araujoronald.application.ports.ReassignTicket;
import com.github.araujoronald.application.ports.TicketRepository;
import com.github.araujoronald.domain.model.Attendant;
import com.github.araujoronald.domain.model.Ticket;

import java.text.MessageFormat;

public class ReassignTicketImpl implements ReassignTicket {

    private final TicketRepository ticketRepository;
    private final AttendantRepository attendantRepository;

    public ReassignTicketImpl(TicketRepository ticketRepository, AttendantRepository attendantRepository) {
        this.ticketRepository = ticketRepository;
        this.attendantRepository = attendantRepository;
    }

    @Override
    public Output execute(Input input) {
        Ticket ticket = ticketRepository.find(input.ticketId())
                .orElseThrow(() -> new TicketNotFoundException(MessageFormat.format("ticket.not.found", input.ticketId())));

        Attendant newAttendant = attendantRepository.find(input.newAttendantId())
                .orElseThrow(() -> new AttendantNotFoundException(MessageFormat.format("attendant.not.found", input.newAttendantId())));

        ticket.assignTo(newAttendant);
        ticketRepository.save(ticket);

        return new Output(ticket.getId());
    }
}