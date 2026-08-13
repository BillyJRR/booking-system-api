package com.reservations.booking.api.service;

import com.reservations.booking.api.dto.request.AppointmentRequestDto;
import com.reservations.booking.api.dto.response.AppointmentResponseDto;
import com.reservations.booking.api.exception.BadRequestException;
import com.reservations.booking.api.exception.ResourceNotFoundException;
import com.reservations.booking.api.model.Appointment;
import com.reservations.booking.api.model.Availability;
import com.reservations.booking.api.model.ServiceEntity;
import com.reservations.booking.api.model.User;
import com.reservations.booking.api.model.enums.AppointmentStatus;
import com.reservations.booking.api.repository.AppointmentRepository;
import com.reservations.booking.api.repository.AvailabilityRepository;
import com.reservations.booking.api.repository.ServiceRepository;
import com.reservations.booking.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final ServiceRepository serviceRepository;
    private final UserRepository userRepository;
    private final AvailabilityRepository availabilityRepository;

    @Transactional
    public AppointmentResponseDto createAppointment(AppointmentRequestDto dto) {
        String userEmail = getAuthenticatedUserEmail();
        User client = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario autenticado no encontrado"));

        ServiceEntity service = serviceRepository.findById(dto.getServiceId())
                .orElseThrow(() -> new ResourceNotFoundException("Servicio no encontrado con el ID: " + dto.getServiceId()));

        User provider = service.getProvider();

        LocalDateTime startDateTime = dto.getStartDateTime();
        LocalDateTime endDateTime = startDateTime.plusMinutes(service.getDurationMinutes());

        validateProviderAvailability(provider.getId(), startDateTime, endDateTime);

        boolean isOverlapping = appointmentRepository.existsOverlappingAppointment(
                provider.getId(),
                startDateTime,
                endDateTime,
                AppointmentStatus.CANCELLED
        );

        if (isOverlapping) {
            throw new BadRequestException("El proveedor ya tiene una cita reservada en el horario seleccionado");
        }

        Appointment appointment = Appointment.builder()
                .startDateTime(startDateTime)
                .endDateTime(endDateTime)
                .status(AppointmentStatus.PENDING)
                .client(client)
                .service(service)
                .build();

        Appointment savedAppointment = appointmentRepository.save(appointment);

        return mapToResponseDto(savedAppointment);
    }

    @Transactional(readOnly = true)
    public List<AppointmentResponseDto> getAppointmentsForCurrentUser() {
        String userEmail = getAuthenticatedUserEmail();
        User client = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        return appointmentRepository.findByClientId(client.getId())
                .stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AppointmentResponseDto> getAllAppointments() {
        return appointmentRepository.findAll()
                .stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public void cancelAppointment(Long appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Cita no encontrada con ID: " + appointmentId));

        appointment.setStatus(AppointmentStatus.CANCELLED);
        appointmentRepository.save(appointment);
    }

    @Transactional
    public AppointmentResponseDto updateAppointmentStatus(Long appointmentId, AppointmentStatus newStatus) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Cita no encontrada con ID: " + appointmentId));

        String userEmail = getAuthenticatedUserEmail();
        User currentUser = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        boolean isProvider = appointment.getService().getProvider().getEmail().equals(userEmail);
        boolean isAdmin = currentUser.getRole().name().equals("ROLE_ADMIN");

        if (!isProvider && !isAdmin) {
            throw new BadRequestException("No tienes permisos para cambiar el estado de esta cita");
        }

        if (appointment.getStatus() == AppointmentStatus.CANCELLED) {
            throw new BadRequestException("No se puede cambiar el estado de una cita que ya fue cancelada");
        }

        appointment.setStatus(newStatus);
        Appointment updatedAppointment = appointmentRepository.save(appointment);
        return mapToResponseDto(updatedAppointment);
    }

    public void validateProviderAvailability(Long providerId, LocalDateTime start, LocalDateTime end) {
        DayOfWeek dayOfWeek = start.getDayOfWeek();
        LocalTime startTime = start.toLocalTime();
        LocalTime endTime = end.toLocalTime();

        List<Availability> availabilities = availabilityRepository.findByProviderIdAndDayOfWeek(providerId,dayOfWeek);

        if (availabilities.isEmpty()) {
            throw new BadRequestException("El proveedor no labora los días " + dayOfWeek);
        }

        boolean fitsInSchedule = availabilities.stream().anyMatch(a ->
                (!startTime.isBefore(a.getStartTime())) && (!endTime.isAfter(a.getEndTime())));

        if (!fitsInSchedule) {
            throw new BadRequestException("La cita esta fuera del horario de atención del proveedor");
        }
    }

    public AppointmentResponseDto mapToResponseDto(Appointment appointment) {
        return AppointmentResponseDto.builder()
                .id(appointment.getId())
                .startDateTime(appointment.getStartDateTime())
                .endDateTime(appointment.getEndDateTime())
                .status(appointment.getStatus())
                .serviceName(appointment.getService().getName())
                .servicePrice(appointment.getService().getPrice())
                .clientName(appointment.getClient().getName())
                .providerName(appointment.getService().getProvider().getName())
                .build();
    }

    private String getAuthenticatedUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            throw new BadRequestException("No hay ningún usuario autenticado en la sesión");
        }

        return authentication.getName();
    }
}

