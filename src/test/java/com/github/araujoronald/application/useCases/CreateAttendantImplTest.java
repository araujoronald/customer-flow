package com.github.araujoronald.application.useCases;

import com.github.araujoronald.application.exceptions.AttendantAlreadyExistsException;
import com.github.araujoronald.application.ports.AttendantRepository;
import com.github.araujoronald.application.ports.CreateAttendant;
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
class CreateAttendantImplTest {

    @Mock
    private AttendantRepository attendantRepository;

    private CreateAttendant createAttendant;

    @Captor
    private ArgumentCaptor<Attendant> attendantArgumentCaptor;

    @BeforeEach
    void setUp() {
        createAttendant = new CreateAttendantImpl(attendantRepository);
    }

    @Test
    @DisplayName("Should create an attendant successfully and return its ID")
    void shouldCreateAttendantSuccessfully() {
        // Given
        var input = new CreateAttendant.Input("John Attendant", "john.attendant@example.com");
        UUID expectedId = UUID.randomUUID();
        Attendant savedAttendant = new Attendant(expectedId, input.name(), input.email());

        when(attendantRepository.save(any(Attendant.class))).thenReturn(savedAttendant);

        // When
        CreateAttendant.Output output = createAttendant.execute(input);

        // Then
        verify(attendantRepository).save(attendantArgumentCaptor.capture());
        Attendant capturedAttendant = attendantArgumentCaptor.getValue();

        assertNotNull(output);
        assertEquals(expectedId, output.attendantId());

        assertEquals(input.name(), capturedAttendant.name());
        assertEquals(input.email(), capturedAttendant.email());
    }

    @Test
    @DisplayName("Should throw NullPointerException for null input")
    void shouldThrowExceptionForNullInput() {
        assertThrows(NullPointerException.class, () -> createAttendant.execute(null));
    }

    @Test
    @DisplayName("Should throw AttendantAlreadyExistsException when email is already in use")
    void shouldThrowExceptionWhenEmailExists() {
        // Given
        var input = new CreateAttendant.Input("John Attendant", "john.attendant@example.com");
        Attendant existingAttendant = Attendant.create("Another John", "john.attendant@example.com");

        when(attendantRepository.findByEmail(input.email())).thenReturn(Optional.of(existingAttendant));

        // When & Then
        AttendantAlreadyExistsException exception = assertThrows(
                AttendantAlreadyExistsException.class,
                () -> createAttendant.execute(input)
        );

        String expectedMessage = MessageFormat.format("attendant.already.exists", input.email());
        assertEquals(expectedMessage, exception.getMessage());
    }
}