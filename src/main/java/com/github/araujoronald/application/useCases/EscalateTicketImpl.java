package com.github.araujoronald.application.useCases;

import com.github.araujoronald.application.exceptions.InvalidEscalationInputException;
import com.github.araujoronald.application.ports.EscalateTicket;
import com.github.araujoronald.application.ports.TicketRepository;
import com.github.araujoronald.domain.model.Ticket;
import com.github.araujoronald.domain.model.TicketStatus;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class EscalateTicketImpl implements EscalateTicket {

    private final TicketRepository ticketRepository;

    public EscalateTicketImpl(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    @Override
    public Output execute(Input input) {
        if (input.scaleGain() <= 0 || input.scaleGain() > 3) {
            throw new InvalidEscalationInputException("escalation.invalid.scale.gain");
        }
        if (input.windowTimeInMinutes() <= 0) {
            throw new InvalidEscalationInputException("escalation.invalid.window.time");
        }

        List<Ticket> pendingTickets = ticketRepository.findAll().stream()
                .filter(ticket -> ticket.getStatus() == TicketStatus.PENDING)
                .collect(Collectors.toList());

        AtomicInteger escalatedCount = new AtomicInteger(0);

        pendingTickets.forEach(ticket -> {
            long elapsedMinutes = Duration.ofMillis(System.currentTimeMillis() - ticket.getCreated().getTime()).toMinutes();
            if (elapsedMinutes < input.windowTimeInMinutes()) {
                return;
            }

            int numberOfWindows = (int) (elapsedMinutes / input.windowTimeInMinutes());
            int basePriority = ticket.getCustomer().qualifier().getPriority();
            int newPriority = basePriority + (numberOfWindows * input.scaleGain());

            if (ticket.updatePriority(newPriority)) {
                ticketRepository.save(ticket);
                escalatedCount.incrementAndGet();
            }
        });

        return new Output(escalatedCount.get());
    }
}