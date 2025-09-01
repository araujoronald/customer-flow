package com.github.araujoronald.infra.api.rest.springboot.controllers;

import com.github.araujoronald.application.ports.AttendantRepository;
import com.github.araujoronald.application.ports.CreateAttendant;
import com.github.araujoronald.application.ports.UpdateAttendant;
import com.github.araujoronald.infra.api.rest.springboot.dtos.UpdateAttendantRequest;
import com.github.araujoronald.infra.providers.UseCaseProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.UUID;

@RestController
@RequestMapping("/attendants")
public class AttendantController {

    private final AttendantRepository attendantRepository;

    @Autowired
    public AttendantController(AttendantRepository attendantRepository) {
        this.attendantRepository = attendantRepository;
    }

    @PostMapping
    public ResponseEntity<CreateAttendant.Output> createAttendant(@RequestBody CreateAttendant.Input input) {
        CreateAttendant useCase = UseCaseProvider.getCreateAttendant(attendantRepository);
        CreateAttendant.Output output = useCase.execute(input);
        return ResponseEntity.status(HttpStatus.CREATED).body(output);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UpdateAttendant.Output> updateAttendant(@PathVariable UUID id, @RequestBody UpdateAttendantRequest request) {
        UpdateAttendant useCase = UseCaseProvider.getUpdateAttendant(attendantRepository);
        UpdateAttendant.Input input = new UpdateAttendant.Input(id, request.name(), request.email());
        UpdateAttendant.Output output = useCase.execute(input);
        return ResponseEntity.ok(output);
    }
}