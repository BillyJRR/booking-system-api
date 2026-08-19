package com.reservations.booking.api.scheduler;

import com.reservations.booking.api.config.RabbitMQConfig;
import com.reservations.booking.api.model.OutboxEmail;
import com.reservations.booking.api.model.enums.OutboxStatus;
import com.reservations.booking.api.repository.OutboxRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class OutboxRetryScheduler {

    private final OutboxRepository outboxRepository;
    private final RabbitTemplate rabbitTemplate;

    @Scheduled(fixedDelay = 300000)
    public void retryPendingEmails() {
        log.info("Init job...");
        List<OutboxEmail> pendingEmails = outboxRepository
                .findByStatusAndRetryCountLessThan(OutboxStatus.PENDING, 3);

        if (!pendingEmails.isEmpty()) {
            log.info("OutboxScheduler: Se encontraron {} correos pendientes/fallidos. Reenviando a la cola...", pendingEmails.size());

            for (OutboxEmail email : pendingEmails) {
                try {
                    rabbitTemplate.convertAndSend(
                            RabbitMQConfig.EXCHANGE_NAME,
                            RabbitMQConfig.ROUTING_KEY,
                            email.getId()
                    );
                    log.info("OutboxScheduler: Reenviado a RabbitMQ - Outbox ID: {}", email.getId());
                } catch (Exception e) {
                    log.error("OutboxScheduler: Error publicando Outbox ID {} en RabbitMQ", email.getId(), e);
                }
            }
        }
    }
}