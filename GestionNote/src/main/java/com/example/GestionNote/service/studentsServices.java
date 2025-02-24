package com.example.GestionNote.service;

import com.example.GestionNote.DTO.StudentDTO;
import com.example.GestionNote.model.Level;
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
    @Autowired
    private LevelServices levelServices;

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
            Level currentlevel = levelServices.getLevelById(updatedStudent.getCurrentLevelId());
            student.setCurrentLevel(currentlevel);
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
        Level currentlevel = levelServices.getLevelById(newStudent.getCurrentLevelId());
        student.setCurrentLevel(currentlevel);
        studentRepository.save(student);
        return true;
    }

    public Student createStudent(Student student) {
        return studentRepository.save(student);
    }

}

