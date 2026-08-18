package com.reservations.booking.api.event;

import lombok.Getter;

@Getter
public class OutboxCreatedEvent {

    private final Long outboxId;

    public OutboxCreatedEvent(Long outboxId) {
        this.outboxId = outboxId;
    }
}