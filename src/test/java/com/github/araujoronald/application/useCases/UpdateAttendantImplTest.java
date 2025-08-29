package com.github.araujoronald.application.useCases;

import com.github.araujoronald.application.exceptions.AttendantNotFoundException;
import com.github.araujoronald.application.ports.AttendantRepository;
import com.github.araujoronald.application.ports.UpdateAttendant;
import com.github.araujoronald.domain.model.Attendant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.text.MessageFormat;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdateAttendantImplTest {

    @Mock
    private AttendantRepository attendantRepository;

    private UpdateAttendant updateAttendant;

    @Captor
    private ArgumentCaptor<Attendant> attendantArgumentCaptor;

    @BeforeEach
    void setUp() {
        updateAttendant = new UpdateAttendantImpl(attendantRepository);
    }

    @Test
    @DisplayName("Should update an attendant successfully")
    void shouldUpdateAttendantSuccessfully() {
        // Given
        UUID attendantId = UUID.randomUUID();
        var input = new UpdateAttendant.Input(attendantId, "Jane Updated", "jane.updated@example.com");
        Attendant existingAttendant = new Attendant(attendantId, "Jane Original", "jane.original@example.com");

        when(attendantRepository.find(attendantId)).thenReturn(Optional.of(existingAttendant));
        when(attendantRepository.save(any(Attendant.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        UpdateAttendant.Output output = updateAttendant.execute(input);

        // Then
        assertNotNull(output);
        assertEquals(attendantId, output.attendantId());

        verify(attendantRepository).save(attendantArgumentCaptor.capture());
        Attendant capturedAttendant = attendantArgumentCaptor.getValue();

        assertEquals(attendantId, capturedAttendant.id());
        assertEquals("Jane Updated", capturedAttendant.name());
        assertEquals("jane.updated@example.com", capturedAttendant.email());
    }

    @Test
    @DisplayName("Should throw AttendantNotFoundException when attendant does not exist")
    void shouldThrowExceptionWhenAttendantNotFound() {
        // Given
        UUID nonExistentId = UUID.randomUUID();
        var input = new UpdateAttendant.Input(nonExistentId, "name", "email");

        when(attendantRepository.find(nonExistentId)).thenReturn(Optional.empty());

        // When & Then
        AttendantNotFoundException exception = assertThrows(
                AttendantNotFoundException.class,
                () -> updateAttendant.execute(input)
        );

        String expectedMessage = MessageFormat.format("attendant.not.found", nonExistentId);
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    @DisplayName("Should throw NullPointerException for null input")
    void shouldThrowExceptionForNullInput() {
        assertThrows(NullPointerException.class, () -> updateAttendant.execute(null));
    }
}