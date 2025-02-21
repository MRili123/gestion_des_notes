package com.example.GestionNote.repository;

import com.example.GestionNote.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StudentRepository extends JpaRepository<Student, Integer> {
    Student findByCne(String cne);

    List<Student> findByDeleted(boolean deleted);
}
