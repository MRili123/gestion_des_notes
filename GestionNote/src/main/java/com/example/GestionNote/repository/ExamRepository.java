package com.example.GestionNote.repository;

import com.example.GestionNote.model.Exam;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExamRepository extends JpaRepository<Exam, Integer> {
    Exam getExamByStudentIdAndElementIdAndSession(int studentId, int elementId, String session);
}
