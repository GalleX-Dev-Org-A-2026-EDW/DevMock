package com.devmock.backend.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.devmock.backend.entity.User;
import com.devmock.backend.entity.en_enum.UserRole;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);
    Optional<User> findByEmailAndDeletedAtIsNull(String email);
    boolean existsByEmail(String email);
    boolean existsByEmailAndDeletedAtIsNull(String email);
    long countByDeletedAtIsNull();
    long countByRoleAndDeletedAtIsNull(UserRole role);
    List<User> findTop10ByDeletedAtIsNullOrderByCreatedAtDesc();
}
