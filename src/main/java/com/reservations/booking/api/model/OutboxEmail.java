package com.reservations.booking.api.model;

import com.reservations.booking.api.model.enums.OutboxStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "outbox_emails")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OutboxEmail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String recipient;
    private String subject;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Enumerated(EnumType.STRING)
    private OutboxStatus status;

    private Integer retryCount;

    private LocalDateTime createdAt;
    private LocalDateTime processedAt;
}