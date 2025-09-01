package com.github.araujoronald.application.useCases;

import com.github.araujoronald.application.exceptions.AttendantAlreadyExistsException;
import com.github.araujoronald.application.ports.AttendantRepository;
import com.github.araujoronald.application.ports.CreateAttendant;
import com.github.araujoronald.domain.model.Attendant;

import java.text.MessageFormat;

public class CreateAttendantImpl implements CreateAttendant {

    private final AttendantRepository repository;

    public CreateAttendantImpl(AttendantRepository attendantRepository) {
        this.repository = attendantRepository;
    }

    @Override
    public Output execute(Input input) {
        Attendant attendant = Attendant.create(input.name(), input.email());

        repository.findByEmail(input.email()).ifPresent(a -> {
            throw new AttendantAlreadyExistsException("attendant.already.exists", a.email());
        });

        final var attendantSaved = this.repository.save(attendant);
        return new Output(attendantSaved.id());
    }
}