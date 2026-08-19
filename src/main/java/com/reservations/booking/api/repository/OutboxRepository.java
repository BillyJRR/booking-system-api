package com.reservations.booking.api.repository;

import com.reservations.booking.api.model.OutboxEmail;
import com.reservations.booking.api.model.enums.OutboxStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OutboxRepository extends JpaRepository<OutboxEmail, Long> {

    List<OutboxEmail> findByStatusAndRetryCountLessThan(OutboxStatus status, int maxRetryCount);
}