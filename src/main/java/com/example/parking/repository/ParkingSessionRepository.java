package com.example.parking.repository;

import com.example.parking.entity.ParkingSession;
import com.example.parking.enums.SessionStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ParkingSessionRepository extends JpaRepository<ParkingSession, Long> {

    boolean existsByVehicleIdAndStatus(Long vehicleId, SessionStatus status);

    boolean existsByVehicleId(Long vehicleId);

    boolean existsBySpotId(Long spotId);

    List<ParkingSession> findAllByOrderByEntryTimeDesc();

    List<ParkingSession> findByStatusOrderByEntryTimeDesc(SessionStatus status);
}
