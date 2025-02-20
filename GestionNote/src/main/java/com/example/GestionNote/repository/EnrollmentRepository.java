package com.example.GestionNote.repository;

import com.example.GestionNote.model.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Integer> {

    @Query("SELECT e FROM Enrollment e WHERE e.module.id = ?1 AND e.academicYear = ?2 AND (e.resultFromSession = ?3 OR e.resultFromSession IS NULL) ")
    List<Enrollment> getEnrollmentsByModuleIdAndAcademicYearAndResultFromSession(Integer moduleId, String academicYear, String resultFromSession);

    List<Enrollment> getEnrollmentsByModuleIdAndAcademicYearAndResultAndResultFromSession(Integer moduleId, String academicYear, String result, String resultFromSession);

    Enrollment getEnrollmentsByModuleIdAndStudentId(Integer moduleId, Integer studentId);

    List<Enrollment> getEnrollmentsByModuleId(Integer moduleId);

    List<Enrollment> getEnrollmentsByModuleIdAndAcademicYear(Integer moduleId, String academicYear);
}
