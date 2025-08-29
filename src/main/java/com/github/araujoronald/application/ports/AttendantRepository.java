package com.github.araujoronald.application.ports;

import com.github.araujoronald.domain.model.Attendant;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public interface AttendantRepository {
    Attendant save(Attendant attendant);
    Optional<Attendant> find(UUID id);
    Optional<Attendant> findByEmail(String email);
    Collection<Attendant> findAll();
}