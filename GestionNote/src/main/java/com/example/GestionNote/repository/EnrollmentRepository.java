package com.example.GestionNote.repository;

import com.example.GestionNote.model.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Integer> {
    List<Enrollment> getEnrollmentsByModuleIdAndAcademicYear(Integer moduleId, String academicYear);
}
