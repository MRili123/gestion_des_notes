package com.example.GestionNote.service;

import com.example.GestionNote.DTO.StudentDTO;
import com.example.GestionNote.model.Student;
import com.example.GestionNote.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class studentsServices {
    @Autowired
    private StudentRepository studentRepository;

    public List<Student> getAllStudents() {
        return studentRepository.findByDeleted(false);
    }

    public Student getStudentByCne(String cne) {
        return studentRepository.findByCne(cne);
    }

    public Student getStudentById(int id) {
        return studentRepository.findById(id).orElse(null);
    }

    public Boolean deleteStudent(int id) {
        Student student = studentRepository.findById(id).orElse(null);
        if (student != null) {
            student.setDeleted(true);
            studentRepository.save(student);
            return true;
        }
        return false;
    }

    public Boolean updateStudent(int id, StudentDTO updatedStudent) {
        Student student = studentRepository.findById(id).orElse(null);
        if (student != null) {
            student.setFirstName(updatedStudent.getFirstName());
            student.setLastName(updatedStudent.getLastName());
            student.setCne(updatedStudent.getCne());
            student.setCurrentLevelId(updatedStudent.getCurrentLevelId());
            studentRepository.save(student);
            return true;
        }
        return false;
    }

    public Boolean createStudent(StudentDTO newStudent) {
        Student student = new Student();
        student.setFirstName(newStudent.getFirstName());
        student.setLastName(newStudent.getLastName());
        student.setCne(newStudent.getCne());
        student.setCurrentLevelId(newStudent.getCurrentLevelId());
        studentRepository.save(student);
        return true;
    }

    public Student createStudent(Student student) {
        return studentRepository.save(student);
    }

}

