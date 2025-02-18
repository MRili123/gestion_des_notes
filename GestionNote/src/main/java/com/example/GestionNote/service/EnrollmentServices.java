package com.example.GestionNote.service;

import com.example.GestionNote.model.Enrollment;
import com.example.GestionNote.repository.EnrollmentRepository;
import org.springframework.stereotype.Service;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

@Service
public class EnrollmentServices {
    private final EnrollmentRepository enrollmentRepository;

    public EnrollmentServices(EnrollmentRepository enrollmentRepository) {
        this.enrollmentRepository = enrollmentRepository;
    }

    public void saveEnrollment(Enrollment enrollment) {
        enrollmentRepository.save(enrollment);
    }

    public List<Enrollment> getEnrollmentsForGradesFile(int moduleId, String academicYear, String result, String resultFromSession, String session) {
        if(session.equals("RATTRAPAGE"))
            return enrollmentRepository.getEnrollmentsByModuleIdAndAcademicYearAndResultAndResultFromSession(moduleId, academicYear, result, resultFromSession);

        return enrollmentRepository.getEnrollmentsByModuleIdAndAcademicYearAndResultFromSession(moduleId, academicYear, resultFromSession);
    }

    public Enrollment getEnrollmentByModuleIdAndStudentId(int ModuleId, int StudentId) {
        return enrollmentRepository.getEnrollmentsByModuleIdAndStudentId(ModuleId, StudentId);
    }

    public List<String> getAcademicYears(){
        List<Enrollment> enrollments = enrollmentRepository.findAll();
        List<String> academicYears = new ArrayList<>(List.of());
        for (Enrollment enrollment : enrollments) {
            if (!academicYears.contains(enrollment.getAcademicYear())) {
                academicYears.add(enrollment.getAcademicYear());
            }
        }
        return academicYears;
    }
}
