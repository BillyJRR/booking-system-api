package com.reservations.booking.api.service;

import com.reservations.booking.api.dto.request.AppointmentRequestDto;
import com.reservations.booking.api.dto.response.AppointmentResponseDto;
import com.reservations.booking.api.exception.BadRequestException;
import com.reservations.booking.api.model.Appointment;
import com.reservations.booking.api.model.Availability;
import com.reservations.booking.api.model.ServiceEntity;
import com.reservations.booking.api.model.User;
import com.reservations.booking.api.model.enums.AppointmentStatus;
import com.reservations.booking.api.repository.AppointmentRepository;
import com.reservations.booking.api.repository.AvailabilityRepository;
import com.reservations.booking.api.repository.ServiceRepository;
import com.reservations.booking.api.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class AppointmentServiceTest {

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private ServiceRepository serviceRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AvailabilityRepository availabilityRepository;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private AppointmentService appointmentService;

    private User client;
    private User provider;
    private ServiceEntity serviceEntity;
    private Availability availability;

    @BeforeEach
    void setUp() {
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        client = User.builder()
                .id(1L)
                .name("Billy")
                .email("billy@example.com")
                .build();

        provider = User.builder()
                .id(2L)
                .name("Dr. Miguel Torres")
                .email("proveedor@example.com")
                .build();

        serviceEntity = ServiceEntity.builder()
                .id(1L)
                .name("Consulta Especializada")
                .durationMinutes(60)
                .price(new BigDecimal("150.00"))
                .provider(provider)
                .build();

        availability = Availability.builder()
                .id(1L)
                .dayOfWeek(DayOfWeek.MONDAY)
                .startTime(LocalTime.of(8, 0))
                .endTime(LocalTime.of(18, 0))
                .provider(provider)
                .build();
    }

    @Test
    @DisplayName("Crear cita exitosamente cuando hay disponibilidad y no exista traslapes")
    void createAppointment_Success() {
        LocalDateTime startDateTime = LocalDateTime.of(2026, 8, 10, 10, 0);
        AppointmentRequestDto request = new AppointmentRequestDto(1L, startDateTime);

        Mockito.when(authentication.getName()).thenReturn("billy@example.com");
        Mockito.when(authentication.isAuthenticated()).thenReturn(true);
        Mockito.when(userRepository.findByEmail("billy@example.com")).thenReturn(Optional.of(client));
        Mockito.when(serviceRepository.findById(1L)).thenReturn(Optional.of(serviceEntity));
        Mockito.when(availabilityRepository.findByProviderIdAndDayOfWeek(2L, DayOfWeek.MONDAY))
                .thenReturn(List.of(availability));
        Mockito.when(appointmentRepository.existsOverlappingAppointment(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any()))
                .thenReturn(false);

        Appointment savedAppointment = Appointment.builder()
                .id(10L)
                .startDateTime(startDateTime)
                .endDateTime(startDateTime.plusMinutes(60))
                .status(AppointmentStatus.PENDING)
                .client(client)
                .service(serviceEntity)
                .build();

        Mockito.when(appointmentRepository.save(Mockito.any(Appointment.class))).thenReturn(savedAppointment);

        AppointmentResponseDto response = appointmentService.createAppointment(request);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(10L, response.getId());
        Assertions.assertEquals("Consulta Especializada", response.getServiceName());
        Assertions.assertEquals(AppointmentStatus.PENDING, response.getStatus());
        Mockito.verify(appointmentRepository, Mockito.times(1)).save(Mockito.any(Appointment.class));
    }

    @Test
    @DisplayName("Lanzar excepción al intentar reservar cuando el horario se traslapa con otra cita")
    void createAppointment_ThrowsException_WhenOverlappingExists() {
        LocalDateTime startDateTime = LocalDateTime.of(2026, 8, 10, 10, 0);
        AppointmentRequestDto request = new AppointmentRequestDto(1L, startDateTime);

        Mockito.when(authentication.getName()).thenReturn("billy@example.com");
        Mockito.when(authentication.isAuthenticated()).thenReturn(true);
        Mockito.when(userRepository.findByEmail("billy@example.com")).thenReturn(Optional.of(client));
        Mockito.when(serviceRepository.findById(1L)).thenReturn(Optional.of(serviceEntity));
        Mockito.when(availabilityRepository.findByProviderIdAndDayOfWeek(2L, DayOfWeek.MONDAY))
                .thenReturn(List.of(availability));

        Mockito.when(appointmentRepository.existsOverlappingAppointment(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any()))
                .thenReturn(true);

        BadRequestException exception = Assertions.assertThrows(
                BadRequestException.class,
                () -> appointmentService.createAppointment(request)
        );

        Assertions.assertTrue(exception.getMessage().contains("ya tiene una cita reservada"));
        Mockito.verify(appointmentRepository, Mockito.never()).save(Mockito.any());
    }
}
