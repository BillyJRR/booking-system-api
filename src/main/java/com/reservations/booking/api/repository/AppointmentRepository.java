package com.reservations.booking.api.repository;

import com.reservations.booking.api.model.Appointment;
import com.reservations.booking.api.model.enums.AppointmentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    @Query("""
        SELECT a FROM Appointment a
        WHERE a.client.id = :clientId
          AND (:status IS NULL OR a.status = :status)
          AND (
            :search IS NULL OR :search = '' OR
            LOWER(a.service.name) LIKE LOWER(CONCAT('%', :search, '%')) OR
            LOWER(a.client.name) LIKE LOWER(CONCAT('%', :search, '%')) OR
            LOWER(a.service.provider.name) LIKE LOWER(CONCAT('%', :search, '%'))
          )
    """)
    Page<Appointment> findByClientIdWithFilters(
            @Param("clientId") Long clientId,
            @Param("status") AppointmentStatus status,
            @Param("search") String search,
            Pageable pageable
    );

    @Query("""
        SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END
        FROM Appointment a
        WHERE a.service.provider.id = :providerId
            AND a.status != :cancelledStatus
            AND (a.startDateTime < :endDateTime AND a.endDateTime > :startDateTime)
    """)
    boolean existsOverlappingAppointment(
            @Param("providerId") Long providerId,
            @Param("startDateTime") Instant startDateTime,
            @Param("endDateTime") Instant endDateTime,
            @Param("cancelledStatus") AppointmentStatus cancelledStatus
    );
}
