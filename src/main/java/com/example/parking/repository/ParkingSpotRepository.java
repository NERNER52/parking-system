package com.example.parking.repository;

import com.example.parking.entity.ParkingSpot;
import com.example.parking.enums.VehicleType;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ParkingSpotRepository extends JpaRepository<ParkingSpot, Long> {

    boolean existsByNumber(String number);

    boolean existsByNumberAndIdNot(String number, Long id);

    List<ParkingSpot> findByOccupiedFalse();

    List<ParkingSpot> findByOccupiedFalseAndType(VehicleType type);

    /** SELECT ... FOR UPDATE — защита от того, что два авто займут одно место одновременно. */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select s from ParkingSpot s where s.id = :id")
    Optional<ParkingSpot> findWithLockById(@Param("id") Long id);
}
