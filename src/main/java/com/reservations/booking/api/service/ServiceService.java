package com.reservations.booking.api.service;

import com.reservations.booking.api.dto.request.ServiceRequestDto;
import com.reservations.booking.api.dto.response.AvailabilityResponseDto;
import com.reservations.booking.api.dto.response.ServiceResponseDto;
import com.reservations.booking.api.exception.BadRequestException;
import com.reservations.booking.api.exception.ResourceNotFoundException;
import com.reservations.booking.api.model.Availability;
import com.reservations.booking.api.model.ServiceEntity;
import com.reservations.booking.api.model.User;
import com.reservations.booking.api.repository.ServiceRepository;
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
public class ServiceService {

    private final ServiceRepository serviceRepository;
    private final UserRepository userRepository;

    @Transactional
    public ServiceResponseDto createService(ServiceRequestDto dto) {
        String userEmail = getAuthenticatedUserEmail();
        User provider = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Proveedor no encontrado"));

        ServiceEntity serviceEntity = ServiceEntity.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .durationMinutes(dto.getDurationMinutes())
                .price(dto.getPrice())
                .provider(provider)
                .build();

        ServiceEntity createdService = serviceRepository.save(serviceEntity);
        return mapToResponseDto(createdService);
    }

    @Transactional(readOnly = true)
    public List<ServiceResponseDto> getAllServices() {
        return serviceRepository.findAll()
                .stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ServiceResponseDto> getServicesByProvider(Long providerId) {
        if (!userRepository.existsById(providerId)) {
            throw new ResourceNotFoundException("Proovedor no encontrado con ID: " + providerId);
        }

        return serviceRepository.findByProviderId(providerId)
                .stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ServiceResponseDto getServiceById(Long id) {
        ServiceEntity serviceEntity = serviceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Servicio no encontrado con ID: " + id));

        return mapToResponseDto(serviceEntity);
    }

    private String getAuthenticatedUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            throw new BadRequestException("No hay ningún usuario autenticado en la sesión");
        }

        return authentication.getName();
    }

    public ServiceResponseDto mapToResponseDto(ServiceEntity serviceEntity) {
        return ServiceResponseDto.builder()
                .id(serviceEntity.getId())
                .name(serviceEntity.getName())
                .description(serviceEntity.getDescription())
                .durationMinutes(serviceEntity.getDurationMinutes())
                .price(serviceEntity.getPrice())
                .providerId(serviceEntity.getProvider().getId())
                .providerName(serviceEntity.getProvider().getName())
                .build();
    }
}
