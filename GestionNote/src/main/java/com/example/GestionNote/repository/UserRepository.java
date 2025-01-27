package com.example.GestionNote.repository;

import com.example.GestionNote.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findAllByUsername(String username);
    Optional<User> findByEmail(String email);
    Optional<User> findById(Integer UserId);

    User findByUsername(String username);
}
