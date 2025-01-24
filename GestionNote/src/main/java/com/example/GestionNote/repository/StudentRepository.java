package com.example.GestionNote.repository;

import com.example.GestionNote.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Integer> {
    List<Student> findByLevelId(Integer levelId); // Find students by level
    Optional<Student> findByCne(String cne); // Find a student by CNE
}
