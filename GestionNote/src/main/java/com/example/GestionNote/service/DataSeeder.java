package com.example.GestionNote.service;

import com.example.GestionNote.model.Professor;
import com.example.GestionNote.repository.ProfessorRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Component
public class DataSeeder implements CommandLineRunner {
    private final ProfessorRepository professorRepository;

    public DataSeeder(ProfessorRepository professorRepository) {
        this.professorRepository = professorRepository;
    }

    @Override
    public void run(String... args) {
        if (professorRepository.count() == 0) { // Only seed if empty
            List<Professor> professors = Arrays.asList(
                    new Professor("John", "Doe", "CIN12345", "johndoe@example.com", "0612345678"),
                    new Professor("Alice", "Smith", "CIN67890", "alicesmith@example.com", "0698765432"),
                    new Professor("Bob", "Brown", "CIN54321", "bobbrown@example.com", "0678901234"),
                    new Professor("Emily", "Johnson", "CIN11111", "emilyjohnson@example.com", "0654321987"),
                    new Professor("Michael", "Davis", "CIN22222", "michaeldavis@example.com", "0611122334"),
                    new Professor("Sarah", "Miller", "CIN33333", "sarahmiller@example.com", "0687654321"),
                    new Professor("David", "Wilson", "CIN44444", "davidwilson@example.com", "0676543210"),
                    new Professor("Laura", "Taylor", "CIN55555", "laurataylor@example.com", "0665432109"),
                    new Professor("James", "Anderson", "CIN66666", "jamesanderson@example.com", "0699998887"),
                    new Professor("Sophia", "Martinez", "CIN77777", "sophiamartinez@example.com", "0601234567")
            );

            professors.forEach(professor -> {
                professor.setCreatedAt(LocalDateTime.now());
                professor.setUpdatedAt(LocalDateTime.now());
                professor.setDeleted(false);
            });

            professorRepository.saveAll(professors);
            System.out.println("Professors seeded successfully!");
        } else {
            System.out.println("Professors table is not empty, skipping seeding.");
        }
    }
}