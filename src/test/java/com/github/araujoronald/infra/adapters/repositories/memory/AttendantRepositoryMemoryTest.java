package com.github.araujoronald.infra.adapters.repositories.memory;

import com.github.araujoronald.application.ports.AttendantRepository;
import com.github.araujoronald.domain.model.Attendant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class AttendantRepositoryMemoryTest {

    private AttendantRepository attendantRepository;

    @BeforeEach
    void setUp() {
        attendantRepository = new AttendantRepositoryMemory();
    }

    @Test
    @DisplayName("Should save an attendant and find it by ID")
    void shouldSaveAndFindAttendantById() {
        // Given
        Attendant attendant = Attendant.create("John Attendant", "john.attendant@example.com");

        // When
        attendantRepository.save(attendant);
        Optional<Attendant> foundAttendantOpt = attendantRepository.find(attendant.id());

        // Then
        assertTrue(foundAttendantOpt.isPresent());
        Attendant foundAttendant = foundAttendantOpt.get();
        assertEquals(attendant.id(), foundAttendant.id());
        assertEquals("John Attendant", foundAttendant.name());
    }

    @Test
    @DisplayName("Should return an empty Optional when attendant is not found")
    void shouldReturnEmptyOptionalWhenAttendantNotFound() {
        // Given
        UUID nonExistentId = UUID.randomUUID();

        // When
        Optional<Attendant> foundAttendantOpt = attendantRepository.find(nonExistentId);

        // Then
        assertFalse(foundAttendantOpt.isPresent());
    }

    @Test
    @DisplayName("Should return all saved attendants")
    void shouldReturnAllSavedAttendants() {
        // Given
        Attendant attendant1 = Attendant.create("Alice", "alice@example.com");
        Attendant attendant2 = Attendant.create("Bob", "bob@example.com");
        attendantRepository.save(attendant1);
        attendantRepository.save(attendant2);

        // When
        Collection<Attendant> allAttendants = attendantRepository.findAll();

        // Then
        assertEquals(2, allAttendants.size());
        assertTrue(allAttendants.contains(attendant1));
        assertTrue(allAttendants.contains(attendant2));
    }
}