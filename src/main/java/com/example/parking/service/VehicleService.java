package com.example.parking.service;

import com.example.parking.dto.VehicleRequest;
import com.example.parking.dto.VehicleResponse;
import com.example.parking.entity.Vehicle;
import com.example.parking.enums.SessionStatus;
import com.example.parking.exception.ConflictException;
import com.example.parking.exception.ResourceNotFoundException;
import com.example.parking.repository.ParkingSessionRepository;
import com.example.parking.repository.VehicleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class VehicleService {

    private final VehicleRepository vehicleRepository;
    private final ParkingSessionRepository sessionRepository;

    public VehicleService(VehicleRepository vehicleRepository, ParkingSessionRepository sessionRepository) {
        this.vehicleRepository = vehicleRepository;
        this.sessionRepository = sessionRepository;
    }

    @Transactional
    public VehicleResponse create(VehicleRequest request) {
        String plate = normalize(request.licensePlate());
        if (vehicleRepository.existsByLicensePlate(plate)) {
            throw new ConflictException("Автомобиль с госномером " + plate + " уже существует");
        }
        Vehicle vehicle = new Vehicle();
        vehicle.setLicensePlate(plate);
        vehicle.setType(request.type());
        vehicle.setOwnerName(request.ownerName());
        return VehicleResponse.from(vehicleRepository.save(vehicle));
    }

    @Transactional(readOnly = true)
    public List<VehicleResponse> getAll() {
        return vehicleRepository.findAll().stream().map(VehicleResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public VehicleResponse getById(Long id) {
        return VehicleResponse.from(findOrThrow(id));
    }

    @Transactional
    public VehicleResponse update(Long id, VehicleRequest request) {
        Vehicle vehicle = findOrThrow(id);
        String plate = normalize(request.licensePlate());

        if (vehicleRepository.existsByLicensePlateAndIdNot(plate, id)) {
            throw new ConflictException("Автомобиль с госномером " + plate + " уже существует");
        }
        if (vehicle.getType() != request.type()
                && sessionRepository.existsByVehicleIdAndStatus(id, SessionStatus.ACTIVE)) {
            throw new ConflictException("Нельзя менять тип автомобиля, пока он находится на парковке");
        }
        vehicle.setLicensePlate(plate);
        vehicle.setType(request.type());
        vehicle.setOwnerName(request.ownerName());
        return VehicleResponse.from(vehicleRepository.save(vehicle));
    }

    @Transactional
    public void delete(Long id) {
        Vehicle vehicle = findOrThrow(id);
        if (sessionRepository.existsByVehicleId(id)) {
            throw new ConflictException("Нельзя удалить автомобиль, у которого есть история парковок");
        }
        vehicleRepository.delete(vehicle);
    }

    private Vehicle findOrThrow(Long id) {
        return vehicleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Автомобиль с id=" + id + " не найден"));
    }

    private String normalize(String plate) {
        return plate.trim().toUpperCase();
    }
}
