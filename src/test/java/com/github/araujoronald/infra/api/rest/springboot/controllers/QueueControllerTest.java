package com.github.araujoronald.infra.api.rest.springboot.controllers;

import com.github.araujoronald.application.ports.TicketRepository;
import com.github.araujoronald.domain.model.*;
import com.github.araujoronald.infra.api.rest.springboot.exceptions.RestExceptionHandler;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(QueueController.class)
@Import(RestExceptionHandler.class)
class QueueControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TicketRepository ticketRepository;

    @Test
    @DisplayName("GET /queue - Should return a list of tickets")
    void viewQueue_shouldReturnListOfTickets() throws Exception {
        // Given
        Customer customer = new Customer(UUID.randomUUID(), "Test Customer", "test@test.com", "+123456789", CustomerQualifier.DEFAULT);
        Attendant attendant = new Attendant(UUID.randomUUID(), "Test Attendant", "attendant@test.com");
        Ticket ticket = Ticket.create(TicketStatus.PENDING, 1, customer, attendant);

        when(ticketRepository.findAll()).thenReturn(List.of(ticket));

        // When & Then
        mockMvc.perform(get("/queue")
                        .header("Accept-Language", "en-US"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].ticketId").value(ticket.getId().toString()))
                .andExpect(jsonPath("$[0].customerName").value("Test Customer"));
    }

    @Test
    @DisplayName("GET /queue - Should return an empty list when no tickets exist")
    void viewQueue_whenNoTickets_shouldReturnEmptyList() throws Exception {
        // Given
        when(ticketRepository.findAll()).thenReturn(Collections.emptyList());

        // When & Then
        mockMvc.perform(get("/queue")
                        .header("Accept-Language", "en-US"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @DisplayName("GET /queue?qualifier=VIP - Should return filtered list of tickets")
    void viewQueue_withQualifierFilter_shouldReturnFilteredList() throws Exception {
        // Given
        Customer vipCustomer = new Customer(UUID.randomUUID(), "VIP Customer", "vip@test.com", "+123456789", CustomerQualifier.VIP);
        Customer defaultCustomer = new Customer(UUID.randomUUID(), "Default Customer", "default@test.com", "+123456789", CustomerQualifier.DEFAULT);
        Attendant attendant = new Attendant(UUID.randomUUID(), "Test Attendant", "attendant@test.com");

        Ticket vipTicket = Ticket.create(TicketStatus.PENDING, 3, vipCustomer, attendant);
        Ticket defaultTicket = Ticket.create(TicketStatus.PENDING, 1, defaultCustomer, attendant);

        when(ticketRepository.findAll()).thenReturn(List.of(vipTicket, defaultTicket));

        // When & Then
        mockMvc.perform(get("/queue")
                        .param("qualifier", "VIP")
                        .header("Accept-Language", "en-US"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].ticketId").value(vipTicket.getId().toString()))
                .andExpect(jsonPath("$[0].customerName").value("VIP Customer"));
    }
}