package com.example.GestionNote.repository;

import com.example.GestionNote.model.Exam;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExamRepository extends JpaRepository<Exam, Integer> {
    Exam getExamByStudentIdAndElementIdAndSessionAndAcademicYear(Integer studentId, Integer elementId, String session, String academicYear);
}
