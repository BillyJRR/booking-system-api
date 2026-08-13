package com.reservations.booking.api.service;

import com.reservations.booking.api.dto.request.AvailabilityRequestDto;
import com.reservations.booking.api.dto.response.AvailabilityResponseDto;
import com.reservations.booking.api.exception.BadRequestException;
import com.reservations.booking.api.exception.ResourceNotFoundException;
import com.reservations.booking.api.model.Availability;
import com.reservations.booking.api.model.User;
import com.reservations.booking.api.repository.AvailabilityRepository;
import com.reservations.booking.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AvailabilityService {

    private final AvailabilityRepository availabilityRepository;
    private final UserRepository userRepository;

    @Transactional
    public AvailabilityResponseDto createAvailability(AvailabilityRequestDto dto) {
        if (dto.getStartTime().isAfter(dto.getEndTime()) || dto.getStartTime().equals(dto.getEndTime())) {
            throw new BadRequestException("La hora de inicio debe ser menor a la hora fin");
        }

        String userEmail = getAuthenticatedUserEmail();
        User provider = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Proveedor no encontrado"));

        Availability availability = Availability.builder()
                .dayOfWeek(dto.getDayOfWeek())
                .startTime(dto.getStartTime())
                .endTime(dto.getEndTime())
                .provider(provider)
                .build();

        Availability savedAvailability = availabilityRepository.save(availability);
        return mapToResponseDto(savedAvailability);
    }

    @Transactional(readOnly = true)
    public List<AvailabilityResponseDto> getAvailabilitiesByProvider(Long providerId) {
        if (!userRepository.existsById(providerId)) {
            throw new ResourceNotFoundException("Proveedor no encontrado con ID: " + providerId);
        }

        return availabilityRepository.findByProviderId(providerId)
                .stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteAvailability(Long id) {
        Availability availability = availabilityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Disponibilidad no encontrada con ID: " + id));

        String userEmail = getAuthenticatedUserEmail();
        if (!availability.getProvider().getEmail().equals(userEmail)) {
            throw new BadRequestException("No tienes permiso para eliminar esta disponibilidad");
        }

        availabilityRepository.delete(availability);
    }

    private String getAuthenticatedUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            throw new BadRequestException("No hay ningún usuario autenticado en la sesión");
        }

        return authentication.getName();
    }

    public AvailabilityResponseDto mapToResponseDto(Availability availability) {
        return AvailabilityResponseDto.builder()
                .id(availability.getId())
                .dayOfWeek(availability.getDayOfWeek())
                .startTime(availability.getStartTime())
                .endTime(availability.getEndTime())
                .providerId(availability.getProvider().getId())
                .providerName(availability.getProvider().getName())
                .build();
    }
}
