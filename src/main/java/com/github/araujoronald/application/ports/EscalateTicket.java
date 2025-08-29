package com.github.araujoronald.application.ports;

public interface EscalateTicket {

    Output execute(Input input);

    record Input(int scaleGain, int windowTimeInMinutes) {}

    record Output(int escalatedTicketsCount) {}
}