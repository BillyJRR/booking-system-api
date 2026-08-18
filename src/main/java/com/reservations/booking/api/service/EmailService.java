package com.reservations.booking.api.service;

public interface EmailService {
    void sendHtmlMessage(String to, String subject, String body);
}