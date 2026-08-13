package com.reservations.booking.api.repository;

import com.reservations.booking.api.model.Availability;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.DayOfWeek;
import java.util.List;

@Repository
public interface AvailabilityRepository extends JpaRepository<Availability, Long> {

    List<Availability> findByProviderIdAndDayOfWeek(Long providerId, DayOfWeek dayOfWeek);
    List<Availability> findByProviderId(Long providerId);
}
