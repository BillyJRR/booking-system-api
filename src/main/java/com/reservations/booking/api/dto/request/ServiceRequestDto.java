package com.reservations.booking.api.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ServiceRequestDto {

    @NotBlank(message = "El nombre del Servicio es obligatorio")
    private String name;

    private String description;

    @NotNull(message = "La duración en minutos es obligatorio")
    @Min(value = 15, message = "La duración mínima es de 15 minutos")
    private Integer durationMinutes;

    @NotNull(message = "El precio es obligatorio")
    @DecimalMin(value = "0.0", inclusive = false, message = "El precio debe ser mayor a 0")
    private BigDecimal price;
}
