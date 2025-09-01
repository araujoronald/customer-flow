package com.github.araujoronald.infra.api.rest.springboot.controllers;

import com.github.araujoronald.application.ports.*;
import com.github.araujoronald.infra.api.rest.springboot.dtos.CancelTicketRequest;
import com.github.araujoronald.infra.api.rest.springboot.dtos.CompleteServiceRequest;
import com.github.araujoronald.infra.api.rest.springboot.dtos.ReassignTicketRequest;
import com.github.araujoronald.infra.providers.UseCaseProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.UUID;

@RestController
@RequestMapping("/tickets")
public class TicketController {

    private final TicketRepository ticketRepository;
    private final CustomerRepository customerRepository;
    private final AttendantRepository attendantRepository;

    @Autowired
    public TicketController(TicketRepository ticketRepository, CustomerRepository customerRepository, AttendantRepository attendantRepository) {
        this.ticketRepository = ticketRepository;
        this.customerRepository = customerRepository;
        this.attendantRepository = attendantRepository;
    }

    @PostMapping
    public ResponseEntity<CreateTicket.Output> createTicket(@RequestBody CreateTicket.Input input) {
        CreateTicket useCase = UseCaseProvider.getCreateTicket(customerRepository, attendantRepository, ticketRepository);
        CreateTicket.Output output = useCase.execute(input);
        return ResponseEntity.status(HttpStatus.CREATED).body(output);
    }

    @PostMapping("/pull-next")
    public ResponseEntity<PullNextTicket.Output> pullNextTicket(@RequestBody PullNextTicket.Input input) {
        PullNextTicket useCase = UseCaseProvider.getPullNextTicket(ticketRepository, attendantRepository);
        PullNextTicket.Output output = useCase.execute(input);
        return ResponseEntity.ok(output);
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<CancelTicket.Output> cancelTicket(@PathVariable UUID id, @RequestBody CancelTicketRequest request) {
        CancelTicket useCase = UseCaseProvider.getCancelTicket(ticketRepository);
        CancelTicket.Input input = new CancelTicket.Input(id, request.reason());
        CancelTicket.Output output = useCase.execute(input);
        return ResponseEntity.ok(output);
    }

    @PostMapping("/{id}/complete")
    public ResponseEntity<CompleteService.Output> completeService(@PathVariable UUID id, @RequestBody CompleteServiceRequest request) {
        CompleteService useCase = UseCaseProvider.getCompleteService(ticketRepository, attendantRepository);
        CompleteService.Input input = new CompleteService.Input(id, request.attendantId(), request.description());
        CompleteService.Output output = useCase.execute(input);
        return ResponseEntity.ok(output);
    }

    @PostMapping("/{id}/reassign")
    public ResponseEntity<ReassignTicket.Output> reassignTicket(@PathVariable UUID id, @RequestBody ReassignTicketRequest request) {
        ReassignTicket useCase = UseCaseProvider.getReassignTicket(ticketRepository, attendantRepository);
        ReassignTicket.Input input = new ReassignTicket.Input(id, request.newAttendantId());
        ReassignTicket.Output output = useCase.execute(input);
        return ResponseEntity.ok(output);
    }

    @PostMapping("/escalate")
    public ResponseEntity<EscalateTicket.Output> escalateTickets(@RequestBody EscalateTicket.Input input) {
        EscalateTicket useCase = UseCaseProvider.getEscalateTicket(ticketRepository);
        EscalateTicket.Output output = useCase.execute(input);
        return ResponseEntity.ok(output);
    }
}