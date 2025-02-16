package com.example.GestionNote.repository;

import com.example.GestionNote.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface StudentRepository extends JpaRepository<Student, Integer> {
    List<Student> findByDeleted(Boolean deleted);
    Student findByCin(String cin);

    // Get a random student
    @Query("SELECT s FROM Student s WHERE s.deleted = :deleted ORDER BY RAND() LIMIT 1")
    Student findRandomByDeleted(Boolean deleted);
}
