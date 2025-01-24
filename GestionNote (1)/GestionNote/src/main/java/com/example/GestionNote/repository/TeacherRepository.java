package com.example.GestionNote.repository;

import com.example.GestionNote.model.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TeacherRepository extends JpaRepository<Teacher, Integer> {
    List<Teacher> findByLastName(String lastName); // Example query by last name
}
