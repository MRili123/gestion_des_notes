package com.example.GestionNote.repository;

import com.example.GestionNote.model.Professor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProfessorRepository extends JpaRepository<Professor, Integer> {
    List<Professor> findByDeletedFalse();
}
