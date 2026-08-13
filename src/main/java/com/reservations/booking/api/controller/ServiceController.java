package com.reservations.booking.api.controller;

import com.reservations.booking.api.dto.common.ApiResponse;
import com.reservations.booking.api.dto.request.ServiceRequestDto;
import com.reservations.booking.api.dto.response.ServiceResponseDto;
import com.reservations.booking.api.service.ServiceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/services")
@RequiredArgsConstructor
public class ServiceController {

    private final ServiceService serviceService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<ServiceResponseDto>>> getAllServices() {
        List<ServiceResponseDto> services = serviceService.getAllServices();
        return ResponseEntity.ok(
                ApiResponse.success(HttpStatus.OK.value(), "Catálogo de servicios recuperado con éxito", services)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ServiceResponseDto>> getServiceById(@PathVariable Long id) {
        ServiceResponseDto service = serviceService.getServiceById(id);
        return ResponseEntity.ok(
                ApiResponse.success(HttpStatus.OK.value(), "Servicio encontrado", service)
        );
    }

    @GetMapping("/provider/{providerId}")
    public ResponseEntity<ApiResponse<List<ServiceResponseDto>>> getServicesByProvider(@PathVariable Long providerId) {
        List<ServiceResponseDto> services = serviceService.getServicesByProvider(providerId);
        return ResponseEntity.ok(
                ApiResponse.success(HttpStatus.OK.value(), "s", services)
        );
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('PROVIDER', 'ADMIN')")
    public ResponseEntity<ApiResponse<ServiceResponseDto>> createdService(@Valid @RequestBody ServiceRequestDto request) {
        ServiceResponseDto service = serviceService.createService(request);
        return new ResponseEntity<>(
                ApiResponse.success(HttpStatus.CREATED.value(), "Servicio creado exitósamente", service),
                HttpStatus.CREATED
        );
    }
}
