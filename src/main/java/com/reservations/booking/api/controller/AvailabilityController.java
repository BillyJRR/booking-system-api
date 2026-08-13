package com.reservations.booking.api.controller;

import com.reservations.booking.api.dto.common.ApiResponse;
import com.reservations.booking.api.dto.request.AvailabilityRequestDto;
import com.reservations.booking.api.dto.response.AvailabilityResponseDto;
import com.reservations.booking.api.service.AvailabilityService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/availabilities")
@RequiredArgsConstructor
public class AvailabilityController {

    private final AvailabilityService availabilityService;

    @PostMapping
    @PreAuthorize("hasAnyRole('PROVIDER', 'ADMIN')")
    public ResponseEntity<ApiResponse<AvailabilityResponseDto>> createAvailability(@Valid @RequestBody AvailabilityRequestDto request) {
        AvailabilityResponseDto created = availabilityService.createAvailability(request);
        return new ResponseEntity<>(
                ApiResponse.success(HttpStatus.CREATED.value(), "Disponibilidad registrada con éxito", created),
                HttpStatus.CREATED
        );
    }

    @GetMapping("/provider/{providerId}")
    public ResponseEntity<ApiResponse<List<AvailabilityResponseDto>>> getAvailabilitiesByProvider(@PathVariable Long providerId) {
        List<AvailabilityResponseDto> availabilities = availabilityService.getAvailabilitiesByProvider(providerId);
        return ResponseEntity.ok(
                ApiResponse.success(HttpStatus.OK.value(), "Disponibilidades recuperadas con éxito", availabilities)
        );
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('PROVIDER', 'ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteAvailability(@PathVariable Long id) {
        availabilityService.deleteAvailability(id);
        return ResponseEntity.ok(
                ApiResponse.success(HttpStatus.OK.value(), "Disponibilidad eliminada exitósamente")
        );
    }
}
