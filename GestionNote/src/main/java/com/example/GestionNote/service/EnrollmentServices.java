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

    public List<Enrollment> getEnrollmentsByModuleAndAcademicYear(int ModuleId, String AcademicYear) {
        return enrollmentRepository.getEnrollmentsByModuleIdAndAcademicYear(ModuleId, AcademicYear);
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
