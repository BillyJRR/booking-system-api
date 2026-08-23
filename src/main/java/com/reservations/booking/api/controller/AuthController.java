package com.reservations.booking.api.controller;

import com.reservations.booking.api.dto.auth.AuthResponseDto;
import com.reservations.booking.api.dto.auth.LoginRequestDto;
import com.reservations.booking.api.dto.auth.RegisterRequestDto;
import com.reservations.booking.api.dto.common.ApiResponse;
import com.reservations.booking.api.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponseDto>> register(@Valid @RequestBody RegisterRequestDto request) {
        return new ResponseEntity<>(
                ApiResponse.success(HttpStatus.CREATED.value(), "Registro exitoso", authService.register(request)),
                HttpStatus.CREATED
        );
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponseDto>> login(@Valid @RequestBody LoginRequestDto request) {
        return ResponseEntity.ok(
                ApiResponse.success(HttpStatus.OK.value(), "Credenciales correctas", authService.login(request)));
    }
}
