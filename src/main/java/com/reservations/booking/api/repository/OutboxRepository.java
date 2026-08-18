package com.reservations.booking.api.repository;

import com.reservations.booking.api.model.OutboxEmail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OutboxRepository extends JpaRepository<OutboxEmail, Long> {
}