package com.devmock.backend.controller;
import com.devmock.backend.dto.CreateUserRequest;
import com.devmock.backend.dto.UpdateUserRequest;
import com.devmock.backend.dto.UserResponse;
import com.devmock.backend.entity.en_enum.AuditAction;
import com.devmock.backend.service.UserService;
import com.devmock.backend.util.AuditHelper;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;
@RestController
@RequestMapping("/api/users")
@PreAuthorize("hasRole('ADMIN')")
public class UserController {
    private final UserService service;
    private final AuditHelper auditHelper;
    public UserController(UserService service, AuditHelper auditHelper) {
        this.service = service;
        this.auditHelper = auditHelper;
    }
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse create(@Valid @RequestBody CreateUserRequest request) {
        UserResponse response = service.create(request);
        auditHelper.log(AuditAction.CREATE, "User", response.getId());
        return response;
    }
    @GetMapping
    public List<UserResponse> list() {
        return service.list();
    }
    @GetMapping("/{id}")
    public UserResponse getById(@PathVariable UUID id) {
        return service.getById(id);
    }
    @GetMapping("/by-email")
    public UserResponse getByEmail(@RequestParam String email) {
        return service.getByEmail(email);
    }
    @PutMapping("/{id}")
    public UserResponse update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateUserRequest request) {
        UserResponse response = service.update(id, request);
        auditHelper.log(AuditAction.UPDATE, "User", id);
        return response;
    }
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        service.delete(id);
        auditHelper.log(AuditAction.DELETE, "User", id);
    }
}