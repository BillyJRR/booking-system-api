package com.reservations.booking.api.service.impl;

import com.resend.Resend;
import com.resend.core.exception.ResendException;
import com.resend.services.emails.model.CreateEmailOptions;
import com.resend.services.emails.model.CreateEmailResponse;
import com.reservations.booking.api.service.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final Resend resend;

    public EmailServiceImpl(@Value("${resend.api-key}") String apiKey) {
        this.resend = new Resend(apiKey);
    }

    @Override
    public void sendHtmlMessage(String to, String subject, String htmlBody) {
        try {
            CreateEmailOptions params = CreateEmailOptions.builder()
                    .from("onboarding@resend.dev")
                    .to(to)
                    .subject(subject)
                    .html(htmlBody)
                    .build();

            CreateEmailResponse response = resend.emails().send(params);
            log.info("Correo enviado exitosamente vía Resend HTTP API. ID: {}", response.getId());

        } catch (ResendException e) {
            log.error("Fallo al enviar correo con Resend API a: {}", to, e);
            throw new RuntimeException("Error enviando email vía Resend HTTP API", e);
        }
    }
}