package com.example.parking.controller;

import com.example.parking.dto.ParkingSpotRequest;
import com.example.parking.dto.ParkingSpotResponse;
import com.example.parking.enums.VehicleType;
import com.example.parking.service.ParkingSpotService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/parking-spots")
@Tag(name = "Parking Spots", description = "Управление парковочными местами")
public class ParkingSpotController {

    private final ParkingSpotService spotService;

    public ParkingSpotController(ParkingSpotService spotService) {
        this.spotService = spotService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Создать парковочное место")
    public ParkingSpotResponse create(@Valid @RequestBody ParkingSpotRequest request) {
        return spotService.create(request);
    }

    @GetMapping
    @Operation(summary = "Список всех парковочных мест")
    public List<ParkingSpotResponse> getAll() {
        return spotService.getAll();
    }

    @GetMapping("/free")
    @Operation(summary = "Свободные места (опционально фильтр по типу)")
    public List<ParkingSpotResponse> getFree(@RequestParam(required = false) VehicleType type) {
        return spotService.getFree(type);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить парковочное место по ID")
    public ParkingSpotResponse getById(@PathVariable Long id) {
        return spotService.getById(id);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Изменить парковочное место")
    public ParkingSpotResponse update(@PathVariable Long id, @Valid @RequestBody ParkingSpotRequest request) {
        return spotService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Удалить парковочное место")
    public void delete(@PathVariable Long id) {
        spotService.delete(id);
    }
}
