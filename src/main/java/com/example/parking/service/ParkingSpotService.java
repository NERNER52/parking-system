package com.example.parking.service;

import com.example.parking.dto.ParkingSpotRequest;
import com.example.parking.dto.ParkingSpotResponse;
import com.example.parking.entity.ParkingSpot;
import com.example.parking.enums.VehicleType;
import com.example.parking.exception.ConflictException;
import com.example.parking.exception.ResourceNotFoundException;
import com.example.parking.repository.ParkingSessionRepository;
import com.example.parking.repository.ParkingSpotRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ParkingSpotService {

    private final ParkingSpotRepository spotRepository;
    private final ParkingSessionRepository sessionRepository;

    public ParkingSpotService(ParkingSpotRepository spotRepository, ParkingSessionRepository sessionRepository) {
        this.spotRepository = spotRepository;
        this.sessionRepository = sessionRepository;
    }

    @Transactional
    public ParkingSpotResponse create(ParkingSpotRequest request) {
        String number = request.number().trim();
        if (spotRepository.existsByNumber(number)) {
            throw new ConflictException("Парковочное место с номером " + number + " уже существует");
        }
        ParkingSpot spot = new ParkingSpot();
        spot.setNumber(number);
        spot.setType(request.type());
        spot.setOccupied(false);
        return ParkingSpotResponse.from(spotRepository.save(spot));
    }

    @Transactional(readOnly = true)
    public List<ParkingSpotResponse> getAll() {
        return spotRepository.findAll().stream().map(ParkingSpotResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public List<ParkingSpotResponse> getFree(VehicleType type) {
        List<ParkingSpot> spots = (type == null)
                ? spotRepository.findByOccupiedFalse()
                : spotRepository.findByOccupiedFalseAndType(type);
        return spots.stream().map(ParkingSpotResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public ParkingSpotResponse getById(Long id) {
        return ParkingSpotResponse.from(findOrThrow(id));
    }

    @Transactional
    public ParkingSpotResponse update(Long id, ParkingSpotRequest request) {
        ParkingSpot spot = findOrThrow(id);
        String number = request.number().trim();

        if (spotRepository.existsByNumberAndIdNot(number, id)) {
            throw new ConflictException("Парковочное место с номером " + number + " уже существует");
        }
        if (spot.isOccupied() && spot.getType() != request.type()) {
            throw new ConflictException("Нельзя менять тип занятого парковочного места");
        }
        spot.setNumber(number);
        spot.setType(request.type());
        return ParkingSpotResponse.from(spotRepository.save(spot));
    }

    @Transactional
    public void delete(Long id) {
        ParkingSpot spot = findOrThrow(id);
        if (spot.isOccupied()) {
            throw new ConflictException("Нельзя удалить занятое парковочное место");
        }
        if (sessionRepository.existsBySpotId(id)) {
            throw new ConflictException("Нельзя удалить место, у которого есть история парковок");
        }
        spotRepository.delete(spot);
    }

    private ParkingSpot findOrThrow(Long id) {
        return spotRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Парковочное место с id=" + id + " не найдено"));
    }
}
