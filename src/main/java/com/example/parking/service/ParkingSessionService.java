package com.example.parking.service;

import com.example.parking.dto.ParkingSessionResponse;
import com.example.parking.dto.SessionEntryRequest;
import com.example.parking.entity.ParkingSession;
import com.example.parking.entity.ParkingSpot;
import com.example.parking.entity.Vehicle;
import com.example.parking.enums.SessionStatus;
import com.example.parking.exception.BusinessRuleException;
import com.example.parking.exception.ConflictException;
import com.example.parking.exception.ResourceNotFoundException;
import com.example.parking.repository.ParkingSessionRepository;
import com.example.parking.repository.ParkingSpotRepository;
import com.example.parking.repository.VehicleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ParkingSessionService {

    private final ParkingSessionRepository sessionRepository;
    private final VehicleRepository vehicleRepository;
    private final ParkingSpotRepository spotRepository;
    private final PricingService pricingService;

    public ParkingSessionService(ParkingSessionRepository sessionRepository,
                                 VehicleRepository vehicleRepository,
                                 ParkingSpotRepository spotRepository,
                                 PricingService pricingService) {
        this.sessionRepository = sessionRepository;
        this.vehicleRepository = vehicleRepository;
        this.spotRepository = spotRepository;
        this.pricingService = pricingService;
    }

    /** Въезд: проверки -> занять место -> создать сессию. */
    @Transactional
    public ParkingSessionResponse enter(SessionEntryRequest request) {
        Vehicle vehicle = vehicleRepository.findById(request.vehicleId())
                .orElseThrow(() -> new ResourceNotFoundException("Автомобиль с id=" + request.vehicleId() + " не найден"));

        // блокировка строки места до конца транзакции
        ParkingSpot spot = spotRepository.findWithLockById(request.spotId())
                .orElseThrow(() -> new ResourceNotFoundException("Парковочное место с id=" + request.spotId() + " не найдено"));

        if (vehicle.getType() != spot.getType()) {
            throw new BusinessRuleException("Тип автомобиля (" + vehicle.getType()
                    + ") не совместим с типом места (" + spot.getType() + ")");
        }
        if (spot.isOccupied()) {
            throw new ConflictException("Парковочное место " + spot.getNumber() + " уже занято");
        }
        if (sessionRepository.existsByVehicleIdAndStatus(vehicle.getId(), SessionStatus.ACTIVE)) {
            throw new ConflictException("Автомобиль " + vehicle.getLicensePlate() + " уже находится на парковке");
        }

        spot.setOccupied(true);
        spotRepository.save(spot);

        ParkingSession session = new ParkingSession();
        session.setVehicle(vehicle);
        session.setSpot(spot);
        session.setEntryTime(LocalDateTime.now());
        session.setStatus(SessionStatus.ACTIVE);

        return ParkingSessionResponse.from(sessionRepository.save(session));
    }

    /** Выезд: зафиксировать время -> посчитать стоимость -> освободить место. */
    @Transactional
    public ParkingSessionResponse exit(Long sessionId) {
        ParkingSession session = findOrThrow(sessionId);
        if (session.getStatus() != SessionStatus.ACTIVE) {
            throw new ConflictException("Сессия " + sessionId + " уже завершена");
        }

        ParkingSpot spot = spotRepository.findWithLockById(session.getSpot().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Парковочное место не найдено"));

        LocalDateTime exitTime = LocalDateTime.now();
        session.setExitTime(exitTime);
        session.setCost(pricingService.calculate(session.getVehicle().getType(), session.getEntryTime(), exitTime));
        session.setStatus(SessionStatus.COMPLETED);

        spot.setOccupied(false);
        spotRepository.save(spot);

        return ParkingSessionResponse.from(sessionRepository.save(session));
    }

    @Transactional(readOnly = true)
    public List<ParkingSessionResponse> getAll(SessionStatus status) {
        List<ParkingSession> sessions = (status == null)
                ? sessionRepository.findAllByOrderByEntryTimeDesc()
                : sessionRepository.findByStatusOrderByEntryTimeDesc(status);
        return sessions.stream().map(ParkingSessionResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public ParkingSessionResponse getById(Long id) {
        return ParkingSessionResponse.from(findOrThrow(id));
    }

    private ParkingSession findOrThrow(Long id) {
        return sessionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Сессия парковки с id=" + id + " не найдена"));
    }
}
