package com.example.GestionNote.service;

import com.example.GestionNote.model.Exam;
import com.example.GestionNote.repository.ExamRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExamServices {
    private final ExamRepository examRepository;

    public ExamServices(ExamRepository examRepository) {
        this.examRepository = examRepository;
    }

    public void saveExam(Exam exam) {
        examRepository.save(exam);
    }

    public Exam getExamByStudentIdAndElementIdAndSessionAndAcademicYear(int studentId, int elementId, String session, String academicYear) {
        return examRepository.getExamByStudentIdAndElementIdAndSessionAndAcademicYear(studentId, elementId, session, academicYear);
    }

    public Exam getTheBestGradedExamForStudent(int studentId, int elementId, String academicYear) {
        Exam rattedExam = getExamByStudentIdAndElementIdAndSessionAndAcademicYear(studentId, elementId, "RATTRAPAGE", academicYear);
        Exam normaleExam = getExamByStudentIdAndElementIdAndSessionAndAcademicYear(studentId, elementId, "NORMALE", academicYear);
        if(rattedExam == null) {
            return normaleExam;
        }
        else {
            if(rattedExam.getGrade() > normaleExam.getGrade()) {
                return rattedExam;
            }
            else{
                return normaleExam;
            }
        }
    }
}
