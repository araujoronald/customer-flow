package com.github.araujoronald.domain.model;

import com.github.araujoronald.application.exceptions.TicketInvalidStateException;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.UUID;

public class Ticket {

    @NotNull UUID id;
    @NotNull TicketStatus status;
    @NotNull @Min(0) Integer priority;
    @NotNull Date created;
    Date start;
    Date end;
    String description;
    String cancellationReason;
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

    public void assignTo(Attendant attendant) {
        this.attendant = attendant;
    }

    public void addDescription(String description) {
        this.description = description;
    }

    public void addCancellationReason(String reason) {
        this.cancellationReason = reason;
    }

    public boolean updatePriority(int newPriority) {
        if (newPriority > this.priority) {
            this.priority = newPriority;
            return true;
        }
        return false;
    }

    public void start(){
        if (this.status != TicketStatus.PENDING) {
            throw new TicketInvalidStateException("ticket.start.invalid.state");
        }
        this.start = new Date();
        this.status = TicketStatus.IN_PROGRESS;
    }

    public void complete(){
        if (this.status != TicketStatus.IN_PROGRESS) {
            throw new TicketInvalidStateException("ticket.complete.invalid.state");
        }
        this.end = new Date();
        this.status = TicketStatus.COMPLETED;
    }

    public void cancel(){
        if (this.status == TicketStatus.COMPLETED || this.status == TicketStatus.CANCELLED) {
            throw new TicketInvalidStateException("ticket.cancel.invalid.state", this.status);
        }
        this.status = TicketStatus.CANCELLED;
    }

    public UUID getId() {
        return id;
    }

    public TicketStatus getStatus() {
        return status;
    }

    public Integer getPriority() {
        return priority;
    }

    public Date getCreated() {
        return created;
    }

    public Date getStart() {
        return start;
    }

    public Date getEnd() {
        return end;
    }

    public Customer getCustomer() {
        return customer;
    }

    public Attendant getAttendant() {
        return attendant;
    }

    public String getDescription(){
        return description;
    }

    public String getCancellationReason() {
        return cancellationReason;
    }
}
