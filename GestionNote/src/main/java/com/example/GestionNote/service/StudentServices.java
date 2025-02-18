package com.example.GestionNote.service;

import com.example.GestionNote.model.Student;
import com.example.GestionNote.repository.StudentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StudentServices {
    private final StudentRepository studentRepository;

    public StudentServices(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    public List<Student> getStudents() {
        return studentRepository.findAll();
    }

    public Student getStudentById(int id) {
        return studentRepository.findById(id).orElse(null);
    }
}
