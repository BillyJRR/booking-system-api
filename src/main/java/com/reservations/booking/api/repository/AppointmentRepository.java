package com.reservations.booking.api.repository;

import com.reservations.booking.api.model.Appointment;
import com.reservations.booking.api.model.enums.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    List<Appointment> findByClientId(Long clientId);

    @Query("""
        SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END
        FROM Appointment a
        WHERE a.service.provider.id = :providerId
            AND a.status != :cancelledStatus
            AND (a.startDateTime < :endDateTime AND a.endDateTime > :startDateTime)
    """)
    boolean existsOverlappingAppointment(
            @Param("providerId") Long providerId,
            @Param("startDateTime")LocalDateTime startDateTime,
            @Param("endDateTime") LocalDateTime endDateTime,
            @Param("cancelledStatus") AppointmentStatus cancelledStatus
    );
}
