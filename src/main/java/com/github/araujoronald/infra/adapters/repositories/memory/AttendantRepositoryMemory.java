package com.github.araujoronald.infra.adapters.repositories.memory;

import com.github.araujoronald.application.ports.AttendantRepository;
import com.github.araujoronald.domain.model.Attendant;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class AttendantRepositoryMemory implements AttendantRepository {

    private final Map<UUID, Attendant> table;
    private final Map<String, Attendant> tableEmailIndex;

    public AttendantRepositoryMemory() {

        this.table = new ConcurrentHashMap<>();
        this.tableEmailIndex = new ConcurrentHashMap<>();
    }

    @Override
    public Attendant save(Attendant attendant) {
        this.table.put(attendant.id(), attendant);
        this.tableEmailIndex.put(attendant.email(), attendant);
        return attendant;
    }

    @Override
    public Optional<Attendant> find(UUID id) {
        return Optional.ofNullable(this.table.get(id));
    }

    @Override
    public Optional<Attendant> findByEmail(String email) {
        return Optional.ofNullable(this.tableEmailIndex.get(email));
    }

    @Override
    public Collection<Attendant> findAll() {
        return this.table.values();
    }
}