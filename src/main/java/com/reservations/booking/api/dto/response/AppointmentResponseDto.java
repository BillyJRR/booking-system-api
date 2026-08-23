package com.reservations.booking.api.dto.response;

import com.reservations.booking.api.model.enums.AppointmentStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppointmentResponseDto {

    private Long id;
    private Instant startDateTime;
    private Instant endDateTime;
    private AppointmentStatus status;
    private String serviceName;
    private BigDecimal servicePrice;
    private String clientName;
    private String providerName;
}
