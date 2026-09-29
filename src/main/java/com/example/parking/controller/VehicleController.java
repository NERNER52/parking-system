package com.example.parking.controller;

import com.example.parking.dto.VehicleRequest;
import com.example.parking.dto.VehicleResponse;
import com.example.parking.service.VehicleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vehicles")
@Tag(name = "Vehicles", description = "Управление автомобилями")
public class VehicleController {

    private final VehicleService vehicleService;

    public VehicleController(VehicleService vehicleService) {
        this.vehicleService = vehicleService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Создать автомобиль")
    public VehicleResponse create(@Valid @RequestBody VehicleRequest request) {
        return vehicleService.create(request);
    }

    @GetMapping
    @Operation(summary = "Список автомобилей")
    public List<VehicleResponse> getAll() {
        return vehicleService.getAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить автомобиль по ID")
    public VehicleResponse getById(@PathVariable Long id) {
        return vehicleService.getById(id);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Изменить автомобиль")
    public VehicleResponse update(@PathVariable Long id, @Valid @RequestBody VehicleRequest request) {
        return vehicleService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Удалить автомобиль")
    public void delete(@PathVariable Long id) {
        vehicleService.delete(id);
    }
}
