package com.github.araujoronald.domain.model;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.text.MessageFormat;
import java.util.UUID;

public class Ticket {

    @NotNull UUID id;
    @NotNull TicketStatus status;
    @NotNull @Min(0) Integer priority;
    @NotNull Date created;
    Date start;
    Date end;
    @NotNull
    Customer customer;
    @NotNull Attendant attendant;

    private Ticket(UUID id, TicketStatus status, Integer priority, Date created, Customer customer, Attendant attendant){
        this.id = id;
        this.status = status;
        this.priority = priority;
        this.created = created;
        this.customer = customer;
        this.attendant = attendant;
    }

    public static Ticket create(TicketStatus status, Integer priority, Customer customer, Attendant attendant){
        return new Ticket(UUID.randomUUID(), status, priority, new Date(), customer, attendant);
    }

    public void start(){
        if (this.status != TicketStatus.PENDING) {
            throw new IllegalStateException("ticket.start.invalid.state");
        }
        this.start = new Date();
        this.status = TicketStatus.IN_PROGRESS;
    }

    public void complete(){
        if (this.status != TicketStatus.IN_PROGRESS) {
            throw new IllegalStateException("ticket.complete.invalid.state");
        }
        this.end = new Date();
        this.status = TicketStatus.COMPLETED;
    }

    public void cancel(){
        if (this.status == TicketStatus.COMPLETED || this.status == TicketStatus.CANCELLED) {
            String message = MessageFormat.format("ticket.cancel.invalid.state", this.status);
            throw new IllegalStateException(message);
        }
        this.status = TicketStatus.CANCELLED;
    }
}
