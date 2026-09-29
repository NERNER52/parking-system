package com.example.parking.controller;

import com.example.parking.dto.ParkingSessionResponse;
import com.example.parking.dto.SessionEntryRequest;
import com.example.parking.enums.SessionStatus;
import com.example.parking.service.ParkingSessionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/parking-sessions")
@Tag(name = "Parking Sessions", description = "Въезд, выезд и история парковок")
public class ParkingSessionController {

    private final ParkingSessionService sessionService;

    public ParkingSessionController(ParkingSessionService sessionService) {
        this.sessionService = sessionService;
    }

    @PostMapping("/entry")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Въезд автомобиля на парковку")
    public ParkingSessionResponse enter(@Valid @RequestBody SessionEntryRequest request) {
        return sessionService.enter(request);
    }

    @PostMapping("/{id}/exit")
    @Operation(summary = "Выезд автомобиля: расчёт стоимости и освобождение места")
    public ParkingSessionResponse exit(@PathVariable Long id) {
        return sessionService.exit(id);
    }

    @GetMapping
    @Operation(summary = "Список сессий (опционально фильтр по статусу ACTIVE/COMPLETED)")
    public List<ParkingSessionResponse> getAll(@RequestParam(required = false) SessionStatus status) {
        return sessionService.getAll(status);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить сессию по ID")
    public ParkingSessionResponse getById(@PathVariable Long id) {
        return sessionService.getById(id);
    }
}
