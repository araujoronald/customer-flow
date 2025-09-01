package com.github.araujoronald.infra.providers;

import com.github.araujoronald.application.ports.*;
import com.github.araujoronald.application.useCases.*;

public class UseCaseProvider {

    private static CreateCustomer createCustomer;
    private static UpdateCustomer updateCustomer;
    private static CreateAttendant createAttendant;
    private static UpdateAttendant updateAttendant;
    private static CreateTicket createTicket;
    private static CancelTicket cancelTicket;
    private static CompleteService completeService;
    private static PullNextTicket pullNextTicket;
    private static ReassignTicket reassignTicket;
    private static EscalateTicket escalateTicket;
    private static ViewQueue viewQueue;

    public static void registerCreateCustomer(CreateCustomer implementation){
        createCustomer = implementation;
    }

    public static void registerUpdateCustomer(UpdateCustomer implementation) {
        updateCustomer = implementation;
    }

    public static void registerCreateAttendant(CreateAttendant implementation) {
        createAttendant = implementation;
    }

    public static void registerUpdateAttendant(UpdateAttendant implementation) {
        updateAttendant = implementation;
    }

    public static void registerCreateTicket(CreateTicket implementation) {
        createTicket = implementation;
    }

    public static void registerCancelTicket(CancelTicket implementation) {
        cancelTicket = implementation;
    }

    public static void registerCompleteService(CompleteService implementation) {
        completeService = implementation;
    }

    public static void registerPullNextTicket(PullNextTicket implementation) {
        pullNextTicket = implementation;
    }

    public static void registerReassignTicket(ReassignTicket implementation) {
        reassignTicket = implementation;
    }

    public static void registerEscalateTicket(EscalateTicket implementation) {
        escalateTicket = implementation;
    }

    public static void registerViewQueue(ViewQueue implementation) {
        viewQueue = implementation;
    }

    public static CreateCustomer getCreateCustomer(CustomerRepository customerRepository){
        if(createCustomer == null)
            createCustomer = new CreateCustomerImpl(customerRepository);
        return createCustomer;
    }

    public static UpdateCustomer getUpdateCustomer(CustomerRepository customerRepository) {
        if (updateCustomer == null)
            updateCustomer = new UpdateCustomerImpl(customerRepository);
        return updateCustomer;
    }

    public static CreateAttendant getCreateAttendant(AttendantRepository attendantRepository) {
        if (createAttendant == null)
            createAttendant = new CreateAttendantImpl(attendantRepository);
        return createAttendant;
    }

    public static UpdateAttendant getUpdateAttendant(AttendantRepository attendantRepository) {
        if (updateAttendant == null)
            updateAttendant = new UpdateAttendantImpl(attendantRepository);
        return updateAttendant;
    }

    public static CreateTicket getCreateTicket(CustomerRepository customerRepository, AttendantRepository attendantRepository, TicketRepository ticketRepository) {
        if (createTicket == null)
            // Assumindo que a implementação CreateTicketImpl existe e recebe estes repositórios
            createTicket = new CreateTicketImpl(ticketRepository, customerRepository, attendantRepository);
        return createTicket;
    }

    public static CancelTicket getCancelTicket(TicketRepository ticketRepository) {
        if (cancelTicket == null)
            // Assumindo que a implementação CancelTicketImpl existe
            cancelTicket = new CancelTicketImpl(ticketRepository);
        return cancelTicket;
    }

    public static CompleteService getCompleteService(TicketRepository ticketRepository, AttendantRepository attendantRepository) {
        if (completeService == null)
            // Assumindo que a implementação CompleteServiceImpl existe
            completeService = new CompleteServiceImpl(ticketRepository);
        return completeService;
    }

    public static PullNextTicket getPullNextTicket(TicketRepository ticketRepository, AttendantRepository attendantRepository) {
        if (pullNextTicket == null)
            // Assumindo que a implementação PullNextTicketImpl existe
            pullNextTicket = new PullNextTicketImpl(ticketRepository, attendantRepository);
        return pullNextTicket;
    }

    public static ReassignTicket getReassignTicket(TicketRepository ticketRepository, AttendantRepository attendantRepository) {
        if (reassignTicket == null)
            // Assumindo que a implementação ReassignTicketImpl existe
            reassignTicket = new ReassignTicketImpl(ticketRepository, attendantRepository);
        return reassignTicket;
    }

    public static EscalateTicket getEscalateTicket(TicketRepository ticketRepository) {
        if (escalateTicket == null)
            // Assumindo que a implementação EscalateTicketImpl existe
            escalateTicket = new EscalateTicketImpl(ticketRepository);
        return escalateTicket;
    }

    public static ViewQueue getViewQueue(TicketRepository ticketRepository) {
        if (viewQueue == null)
            // Assumindo que a implementação ViewQueueImpl existe
            viewQueue = new ViewQueueImpl(ticketRepository);
        return viewQueue;
    }
}
