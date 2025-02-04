package com.example.GestionNote.repository;

import com.example.GestionNote.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);

    Optional<User> findAllByUsername(String username);
    Optional<User> findByEmail(String email);
    Optional<User> findById(Integer UserId);

    Optional<User> findByUsername(String username);
}
