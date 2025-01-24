package com.example.GestionNote.repository;

import com.example.GestionNote.model.Grade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GradeRepository extends JpaRepository<Grade, Integer> {
    List<Grade> findByStudentId(Integer studentId); // Find grades by student
    List<Grade> findByElementId(Integer elementId); // Find grades by element
}
