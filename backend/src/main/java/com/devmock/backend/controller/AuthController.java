package com.devmock.backend.controller;

import com.devmock.backend.dto.AuthResponse;
import com.devmock.backend.dto.LoginRequest;
import com.devmock.backend.dto.RegisterRequest;
import com.devmock.backend.entity.DifficultyLevel;
import com.devmock.backend.entity.User;
import com.devmock.backend.entity.en_enum.UserRole;
import com.devmock.backend.exception.EmailAlreadyExistsException;
import com.devmock.backend.exception.ResourceNotFoundException;
import com.devmock.backend.repository.DifficultyLevelRepository;
import com.devmock.backend.repository.UserRepository;
import com.devmock.backend.security.JwtService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
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
    private final DifficultyLevelRepository difficultyLevelRepository;

    public AuthController(UserRepository userRepo,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authManager,
            JwtService jwtService,
            DifficultyLevelRepository difficultyLevelRepository) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
        this.authManager = authManager;
        this.jwtService = jwtService;
        this.difficultyLevelRepository = difficultyLevelRepository;
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
        if (req.getCurrentLevelId() != null) {
            DifficultyLevel level = difficultyLevelRepository.findById(req.getCurrentLevelId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "DifficultyLevel " + req.getCurrentLevelId() + " not found"));
            user.setCurrentLevel(level);
        }
        user.setIsActive(true);
        user.setIsVerified(false);
        userRepo.save(user);
        String token = jwtService.generateToken(user.getEmail());
        return new AuthResponse(token, user.getEmail(), user.getFullName(), user.getRole());
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest req) {
        User user = userRepo.findByEmail(req.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No existe una cuenta con el email " + req.getEmail()));
        try {
            authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            req.getEmail(), req.getPassword()));
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("Contraseña incorrecta");
        }
        String token = jwtService.generateToken(user.getEmail());
        return new AuthResponse(token, user.getEmail(), user.getFullName(), user.getRole());
    }
}