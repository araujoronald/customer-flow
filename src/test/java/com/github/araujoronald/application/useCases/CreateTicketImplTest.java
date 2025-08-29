package com.github.araujoronald.application.useCases;

import com.github.araujoronald.application.exceptions.AttendantNotFoundException;
import com.github.araujoronald.application.exceptions.CustomerNotFoundException;
import com.github.araujoronald.application.ports.AttendantRepository;
import com.github.araujoronald.application.ports.CreateTicket;
import com.github.araujoronald.application.ports.CustomerRepository;
import com.github.araujoronald.application.ports.TicketRepository;
import com.github.araujoronald.domain.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateTicketImplTest {

    @Mock
    private TicketRepository ticketRepository;
    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private AttendantRepository attendantRepository;

    private CreateTicket createTicket;

    @Captor
    private ArgumentCaptor<Ticket> ticketArgumentCaptor;

    @BeforeEach
    void setUp() {
        createTicket = new CreateTicketImpl(ticketRepository, customerRepository, attendantRepository);
    }

    @Test
    @DisplayName("Should create a ticket successfully with priority from customer qualifier")
    void shouldCreateTicketSuccessfully() {
        // Given
        UUID customerId = UUID.randomUUID();
        UUID attendantId = UUID.randomUUID();
        var input = new CreateTicket.Input(customerId, attendantId);

        Customer customer = new Customer(customerId, "John Doe", "john@test.com", "+1", CustomerQualifier.VIP); // VIP priority is 3
        Attendant attendant = new Attendant(attendantId, "Jane Smith", "jane@test.com");

        when(customerRepository.find(customerId)).thenReturn(Optional.of(customer));
        when(attendantRepository.find(attendantId)).thenReturn(Optional.of(attendant));
        when(ticketRepository.save(any(Ticket.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        CreateTicket.Output output = createTicket.execute(input);

        // Then
        assertNotNull(output);
        assertNotNull(output.ticketId());

        verify(ticketRepository).save(ticketArgumentCaptor.capture());
        Ticket capturedTicket = ticketArgumentCaptor.getValue();

        assertEquals(TicketStatus.PENDING, capturedTicket.getStatus());
        assertEquals(3, capturedTicket.getPriority()); // VIP priority
        assertEquals(customer, capturedTicket.getCustomer());
        assertEquals(attendant, capturedTicket.getAttendant());
    }

    @Test
    @DisplayName("Should throw CustomerNotFoundException when customer does not exist")
    void shouldThrowWhenCustomerNotFound() {
        // Given
        var input = new CreateTicket.Input(UUID.randomUUID(), UUID.randomUUID());
        when(customerRepository.find(any(UUID.class))).thenReturn(Optional.empty());

        // When & Then
        assertThrows(CustomerNotFoundException.class, () -> createTicket.execute(input));
    }

    @Test
    @DisplayName("Should throw AttendantNotFoundException when attendant does not exist")
    void shouldThrowWhenAttendantNotFound() {
        // Given
        Customer customer = Customer.create("Test", "test@test.com", "+1", CustomerQualifier.DEFAULT);
        var input = new CreateTicket.Input(customer.id(), UUID.randomUUID());

        when(customerRepository.find(customer.id())).thenReturn(Optional.of(customer));
        when(attendantRepository.find(any(UUID.class))).thenReturn(Optional.empty());

        // When & Then
        assertThrows(AttendantNotFoundException.class, () -> createTicket.execute(input));
    }
}