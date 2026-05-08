package com.devmock.backend.controller;

import com.devmock.backend.dto.AuthResponse;
import com.devmock.backend.dto.LoginRequest;
import com.devmock.backend.dto.RegisterRequest;
import com.devmock.backend.entity.User;
import com.devmock.backend.entity.en_enum.UserRole;
import com.devmock.backend.exception.EmailAlreadyExistsException;
import com.devmock.backend.repository.UserRepository;
import com.devmock.backend.security.JwtService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authManager;
    private final JwtService jwtService;

    public AuthController(UserRepository userRepo,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authManager,
            JwtService jwtService) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
        this.authManager = authManager;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthResponse register(@Valid @RequestBody RegisterRequest req) {
        if (userRepo.existsByEmail(req.getEmail())) {
            throw new EmailAlreadyExistsException(
                    "Email " + req.getEmail() + " is already registered");
        }
        User user = new User();
        user.setEmail(req.getEmail());
        user.setPasswordHash(passwordEncoder.encode(req.getPassword()));
        user.setFullName(req.getFullName());
        user.setRole(req.getRole() != null ? req.getRole() : UserRole.STUDENT);
        user.setAvatarUrl(req.getAvatarUrl());
        user.setProfessionalExperienceYears(req.getProfessionalExperienceYears());
        user.setIsActive(true);
        user.setIsVerified(false);
        userRepo.save(user);
        String token = jwtService.generateToken(user.getEmail());
        return new AuthResponse(token, user.getEmail(), user.getFullName());
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest req) {
        Authentication auth = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        req.getEmail(), req.getPassword()));
        User user = userRepo.findByEmail(auth.getName())
                .orElseThrow();
        String token = jwtService.generateToken(user.getEmail());
        return new AuthResponse(token, user.getEmail(), user.getFullName());
    }
}