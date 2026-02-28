package org.example.inventorysmart.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.inventorysmart.dto.request.AuthRequest;
import org.example.inventorysmart.dto.response.ApiResponse;
import org.example.inventorysmart.dto.response.AuthResponse;
import org.example.inventorysmart.entity.User;
import org.example.inventorysmart.repository.UserRepository;
import org.example.inventorysmart.security.JwtService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder; // In case we need register later

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody AuthRequest request) {
        // Authenticate credentials
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

        // Fetch user from DB
        User user = userRepository.findByUsername(request.getUsername()).orElseThrow();

        // Generate JWT
        String token = jwtService.generateToken(user);

        AuthResponse authResponse = AuthResponse.builder()
                .token(token)
                .message("Login successful")
                .build();

        ApiResponse<AuthResponse> apiResponse = ApiResponse.<AuthResponse>builder()
                .code(200)
                .message("Success")
                .data(authResponse)
                .build();

        return ResponseEntity.ok(apiResponse);
    }
}
