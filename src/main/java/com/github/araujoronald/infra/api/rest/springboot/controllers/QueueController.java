package com.github.araujoronald.infra.api.rest.springboot.controllers;

import com.github.araujoronald.application.ports.TicketRepository;
import com.github.araujoronald.application.ports.ViewQueue;
import com.github.araujoronald.domain.model.CustomerQualifier;
import com.github.araujoronald.infra.providers.UseCaseProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/queue")
public class QueueController {

    private final TicketRepository ticketRepository;

    @Autowired
    public QueueController(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    @GetMapping
    public ResponseEntity<List<ViewQueue.QueueTicket>> viewQueue(
            @RequestParam Optional<UUID> customerId,
            @RequestParam Optional<CustomerQualifier> qualifier) {
        ViewQueue useCase = UseCaseProvider.getViewQueue(ticketRepository);
        ViewQueue.Input input = new ViewQueue.Input(customerId, qualifier);
        List<ViewQueue.QueueTicket> output = useCase.execute(input);
        return ResponseEntity.ok(output);
    }
}