package com.github.araujoronald.application.useCases;

import com.github.araujoronald.application.exceptions.AttendantNotFoundException;
import com.github.araujoronald.application.ports.AttendantRepository;
import com.github.araujoronald.application.ports.UpdateAttendant;
import com.github.araujoronald.domain.model.Attendant;

import java.text.MessageFormat;

public class UpdateAttendantImpl implements UpdateAttendant {

    private final AttendantRepository repository;

    public UpdateAttendantImpl(AttendantRepository attendantRepository) {
        this.repository = attendantRepository;
    }

    @Override
    public Output execute(Input input) {
        Attendant existingAttendant = repository.find(input.attendantId())
                .orElseThrow(() -> {
                    String message = MessageFormat.format("attendant.not.found", input.attendantId());
                    return new AttendantNotFoundException(message);
                });

        Attendant updatedAttendant = new Attendant(
                existingAttendant.id(),
                input.name(),
                input.email()
        );

        final var attendantSaved = this.repository.save(updatedAttendant);
        return new Output(attendantSaved.id());
    }
}