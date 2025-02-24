package com.example.GestionNote.service;

import com.example.GestionNote.model.StudentDataHistory;
import com.example.GestionNote.repository.StudentDataHistoryRepository;
import org.springframework.stereotype.Service;

@Service
public class StudentDataHistoryService {
    private final StudentDataHistoryRepository studentDataHistoryRepository;

    public StudentDataHistoryService(StudentDataHistoryRepository studentDataHistoryRepository) {
        this.studentDataHistoryRepository = studentDataHistoryRepository;
    }

    public void save(StudentDataHistory studentDataHistory) {
        studentDataHistoryRepository.save(studentDataHistory);
    }
}
