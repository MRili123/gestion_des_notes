package com.example.GestionNote.repository;

import com.example.GestionNote.model.Role;
import com.example.GestionNote.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    boolean existsByCin(String cin);
    Optional<User> findAllByUsername(String username);
    Optional<User> findByEmail(String email);
    Optional<User> findById(Integer UserId);
    long countByRole(Role role);
    Optional<User> findByUsername(String username);
}
