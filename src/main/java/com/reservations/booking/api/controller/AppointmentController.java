package com.reservations.booking.api.controller;

import com.reservations.booking.api.dto.common.ApiResponse;
import com.reservations.booking.api.dto.request.AppointmentRequestDto;
import com.reservations.booking.api.dto.response.AppointmentResponseDto;
import com.reservations.booking.api.model.enums.AppointmentStatus;
import com.reservations.booking.api.service.AppointmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;

    @GetMapping("/my-appointments")
    @PreAuthorize("hasAnyRole('CLIENT', 'ADMIN')")
    public ResponseEntity<ApiResponse<Page<AppointmentResponseDto>>> getAppointments(
            @RequestParam(required = false) AppointmentStatus status,
            @RequestParam(required = false) String search,
            @PageableDefault(size = 10, sort = "startDateTime", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<AppointmentResponseDto> appointments = appointmentService.getAppointmentsForCurrentUser(
                status,
                search,
                pageable
        );

        return ResponseEntity.ok(
                ApiResponse.success(HttpStatus.OK.value(), "Citas recuperadas con éxito", appointments)
        );
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('CLIENT', 'ADMIN')")
    public ResponseEntity<ApiResponse<AppointmentResponseDto>> createAppointment(@Valid @RequestBody AppointmentRequestDto request) {
        AppointmentResponseDto created = appointmentService.createAppointment(request);
        return new ResponseEntity<>(
                ApiResponse.success(HttpStatus.CREATED.value(), "Cita reservada con éxito", created),
                HttpStatus.CREATED
        );
    }

    @GetMapping("/admin/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<AppointmentResponseDto>>> getAllAppointmentsForAdmin() {
        List<AppointmentResponseDto> appointments = appointmentService.getAllAppointments();
        return ResponseEntity.ok(
                ApiResponse.success(HttpStatus.OK.value(), "Todas las citas del sistema recuperadas con éxito", appointments)
        );
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('CLIENT', 'ADMIN')")
    public ResponseEntity<ApiResponse<Void>> cancelAppointment(@PathVariable Long id) {
        appointmentService.cancelAppointment(id);
        return ResponseEntity.ok(
                ApiResponse.success(HttpStatus.OK.value(), "La cita ha sido cancelada exitósamente")
        );
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('PROVIDER', 'ADMIN')")
    public ResponseEntity<ApiResponse<AppointmentResponseDto>> updateStatus(@PathVariable Long id, @RequestParam AppointmentStatus status) {
        AppointmentResponseDto update = appointmentService.updateAppointmentStatus(id, status);
        return ResponseEntity.ok(
                ApiResponse.success(HttpStatus.OK.value(), "Estado de la cita actualizado con éxito", update)
        );
    }
}
