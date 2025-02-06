package com.example.GestionNote.repository;

import com.example.GestionNote.model.Professor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProfessorRepository extends JpaRepository<Professor, Integer> {
    List<Professor> findByDeleted(Boolean deleted);
    Professor findByCin(String cin);

    // Get a random professor
    @Query("SELECT p FROM Professor p WHERE p.deleted = :deleted ORDER BY RAND() LIMIT 1")
    Professor findRandomByDeleted(Boolean deleted);
}
