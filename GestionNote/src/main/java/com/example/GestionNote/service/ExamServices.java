package com.example.GestionNote.service;

import com.example.GestionNote.model.Exam;
import com.example.GestionNote.repository.ExamRepository;
import org.springframework.stereotype.Service;

@Service
public class ExamServices {
    private final ExamRepository examRepository;

    public ExamServices(ExamRepository examRepository) {
        this.examRepository = examRepository;
    }

    public void saveExam(Exam exam) {
        examRepository.save(exam);
    }

    public Exam getExamByStudentIdAndElementIdAndSession(int studentId, int elementId, String session) {
        return examRepository.getExamByStudentIdAndElementIdAndSession(studentId, elementId, session);
    }
}
