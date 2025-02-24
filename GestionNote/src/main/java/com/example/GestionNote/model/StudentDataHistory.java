package com.example.GestionNote.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "students_data_history")
public class StudentDataHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String cne;
    private String firstName;
    private String lastName;
    private LocalDateTime updatedAt = LocalDateTime.now();

    // constructors
    public StudentDataHistory() {
    }

    public StudentDataHistory(String cne, String firstName, String lastName, User admin, Student student, Level currentLevel) {
        this.cne = cne;
        this.firstName = firstName;
        this.lastName = lastName;
        this.user = admin;
        this.student = student;
        this.currentLevel = currentLevel;
    }

    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "modified_by")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id")
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "current_level_id")
    private Level currentLevel;

}
