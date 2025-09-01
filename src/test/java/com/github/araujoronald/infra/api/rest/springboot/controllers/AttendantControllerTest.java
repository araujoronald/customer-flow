package com.github.araujoronald.infra.api.rest.springboot.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.araujoronald.application.ports.AttendantRepository;
import com.github.araujoronald.application.ports.CreateAttendant;
import com.github.araujoronald.domain.model.Attendant;
import com.github.araujoronald.infra.api.rest.springboot.dtos.UpdateAttendantRequest;
import com.github.araujoronald.infra.api.rest.springboot.exceptions.RestExceptionHandler;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AttendantController.class)
@Import(RestExceptionHandler.class)
class AttendantControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AttendantRepository attendantRepository;

    @Autowired
    private MessageSource messageSource;

    @Test
    @DisplayName("POST /attendants - Should create an attendant and return 201 Created")
    void createAttendant_shouldReturnCreated() throws Exception {
        // Given
        var input = new CreateAttendant.Input("John Attendant", "john.doe@example.com");
        var attendantId = UUID.randomUUID();
        var savedAttendant = new Attendant(attendantId, input.name(), input.email());

        when(attendantRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(attendantRepository.save(any(Attendant.class))).thenReturn(savedAttendant);

        // When & Then
        mockMvc.perform(post("/attendants")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input))
                        .header("Accept-Language", "en-US"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.attendantId").value(attendantId.toString()));
    }

    @Test
    @DisplayName("POST /attendants - Should return 409 Conflict when attendant email already exists")
    void createAttendant_whenEmailExists_shouldReturnConflict() throws Exception {
        // Given
        var input = new CreateAttendant.Input("Jane Attendant", "jane.doe@example.com");
        var existingAttendant = Attendant.create(input.name(), input.email());

        when(attendantRepository.findByEmail(input.email())).thenReturn(Optional.of(existingAttendant));

        String expectedMessage = messageSource.getMessage(
                "attendant.already.exists", new Object[]{input.email()}, Locale.US);

        // When & Then
        mockMvc.perform(post("/attendants")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input))
                        .header("Accept-Language", "en-US"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409));
    }

    @Test
    @DisplayName("PUT /attendants/{id} - Should update an attendant and return 200 OK")
    void updateAttendant_shouldReturnOk() throws Exception {
        // Given
        var attendantId = UUID.randomUUID();
        var request = new UpdateAttendantRequest("John Updated", "john.new@example.com");
        var existingAttendant = new Attendant(attendantId, "John Original", "john.old@example.com");
        var updatedAttendant = new Attendant(attendantId, request.name(), request.email());

        when(attendantRepository.find(attendantId)).thenReturn(Optional.of(existingAttendant));
        when(attendantRepository.save(any(Attendant.class))).thenReturn(updatedAttendant);

        // When & Then
        mockMvc.perform(put("/attendants/{id}", attendantId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Accept-Language", "en-US"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.attendantId").value(attendantId.toString()));
    }

    @Test
    @DisplayName("PUT /attendants/{id} - Should return 404 Not Found when attendant does not exist")
    void updateAttendant_whenNotFound_shouldReturnNotFound() throws Exception {
        // Given
        var attendantId = UUID.randomUUID();
        var request = new UpdateAttendantRequest("John Updated", "john.new@example.com");

        when(attendantRepository.find(attendantId)).thenReturn(Optional.empty());

        String expectedMessage = messageSource.getMessage(
                "attendant.not.found", new Object[]{attendantId}, Locale.US);

        // When & Then
        mockMvc.perform(put("/attendants/{id}", attendantId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Accept-Language", "en-US"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }
}