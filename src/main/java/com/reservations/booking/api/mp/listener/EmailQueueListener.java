package com.reservations.booking.api.mp.listener;

import com.reservations.booking.api.config.RabbitMQConfig;
import com.reservations.booking.api.model.OutboxEmail;
import com.reservations.booking.api.model.enums.OutboxStatus;
import com.reservations.booking.api.repository.OutboxRepository;
import com.reservations.booking.api.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Component
@RequiredArgsConstructor
@Slf4j
public class EmailQueueListener {

    private final OutboxRepository outboxRepository;
    private final EmailService emailService;

    @RabbitListener(queues = RabbitMQConfig.QUEUE_NAME)
    @Transactional
    public void processEmailMessage(Long outboxId) {
        OutboxEmail email = outboxRepository.findById(outboxId)
                .orElseThrow(() -> new RuntimeException("Outbox message no encontrado: " + outboxId));

        try {
            emailService.sendHtmlMessage(email.getRecipient(), email.getSubject(), email.getContent());

            email.setStatus(OutboxStatus.SENT);
            email.setProcessedAt(Instant.now());
            outboxRepository.save(email);

        } catch (Exception e) {
            log.error("Error enviando correo con Outbox ID: {}", outboxId, e);

            email.setRetryCount(email.getRetryCount() + 1);
            email.setErrorMessage(e.getMessage());
            if (email.getRetryCount() >= 3) {
                email.setStatus(OutboxStatus.FAILED);
            }
            outboxRepository.save(email);
        }
    }
}