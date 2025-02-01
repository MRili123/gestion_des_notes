package com.example.GestionNote.repository;

import com.example.GestionNote.model.Professor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProfessorRepository extends JpaRepository<Professor, Integer> {
    List<Professor> findByDeleted(Boolean deleted);
    Professor findByCin(String cin);
}
