package com.example.GestionNote.repository;

import com.example.GestionNote.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface StudentRepository extends JpaRepository<Student, Integer> {
    Student findByCne(String cne);
    Student getStudentById(Integer id);
    List<Student> findByDeleted(boolean deleted);
}
