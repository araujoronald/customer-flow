package com.github.araujoronald.infra.api.rest.springboot.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.araujoronald.application.ports.AttendantRepository;
import com.github.araujoronald.application.ports.CreateTicket;
import com.github.araujoronald.application.ports.CustomerRepository;
import com.github.araujoronald.application.ports.PullNextTicket;
import com.github.araujoronald.application.ports.CompleteService;
import com.github.araujoronald.application.ports.TicketRepository;
import com.github.araujoronald.domain.model.*;
import com.github.araujoronald.infra.api.rest.springboot.dtos.CancelTicketRequest;
import com.github.araujoronald.infra.api.rest.springboot.exceptions.RestExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.github.araujoronald.infra.api.rest.springboot.dtos.CompleteServiceRequest;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TicketController.class)
@Import(RestExceptionHandler.class)
class TicketControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TicketRepository ticketRepository;

    @MockBean
    private CustomerRepository customerRepository;

    @MockBean
    private AttendantRepository attendantRepository;

    @Autowired
    private MessageSource messageSource;

    private Customer customer;
    private Attendant attendant;

    @BeforeEach
    void setUp() {
        customer = new Customer(UUID.randomUUID(), "Test Customer", "test@test.com", "+123456789", CustomerQualifier.DEFAULT);
        attendant = new Attendant(UUID.randomUUID(), "Test Attendant", "attendant@test.com");
    }

    @Test
    @DisplayName("POST /tickets - Should create a ticket and return 201 Created")
    void createTicket_shouldReturnCreated() throws Exception {
        // Given
        var input = new CreateTicket.Input(customer.id(), attendant.id());
        var ticket = Ticket.create(TicketStatus.PENDING, 1, customer, attendant);

        when(customerRepository.find(customer.id())).thenReturn(Optional.of(customer));
        when(attendantRepository.find(attendant.id())).thenReturn(Optional.of(attendant));
        when(ticketRepository.save(any(Ticket.class))).thenReturn(ticket);

        // When & Then
        mockMvc.perform(post("/tickets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input))
                        .header("Accept-Language", "en-US"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.ticketId").value(ticket.getId().toString()));
    }

    @Test
    @DisplayName("POST /tickets/pull-next - Should pull the next ticket and return 200 OK")
    void pullNextTicket_shouldReturnOk() throws Exception {
        // Given
        var ticket = Ticket.create(TicketStatus.PENDING, 10, customer, attendant);
        when(ticketRepository.findAll()).thenReturn(List.of(ticket));
        when(attendantRepository.find(attendant.id())).thenReturn(Optional.of(attendant));
        when(ticketRepository.save(any(Ticket.class))).thenReturn(ticket);

        var input = new PullNextTicket.Input(attendant.id());

        // When & Then
        mockMvc.perform(post("/tickets/pull-next")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input))
                        .header("Accept-Language", "en-US"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ticketId").value(ticket.getId().toString()))
                .andExpect(jsonPath("$.customerName").value(customer.name()));
    }

    @Test
    @DisplayName("POST /tickets/{id}/cancel - Should cancel a ticket and return 200 OK")
    void cancelTicket_shouldReturnOk() throws Exception {
        // Given
        var ticket = Ticket.create(TicketStatus.PENDING, 1, customer, attendant);
        var request = new CancelTicketRequest("Customer requested cancellation");

        when(ticketRepository.find(ticket.getId())).thenReturn(Optional.of(ticket));
        when(ticketRepository.save(any(Ticket.class))).thenReturn(ticket);

        // When & Then
        mockMvc.perform(post("/tickets/{id}/cancel", ticket.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Accept-Language", "en-US"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ticketId").value(ticket.getId().toString()));
    }
}