package com.example.GestionNote.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "deliberations")
public class Deliberation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Double finalGrade;
    private Boolean passed;
    private Integer rank;
    private String academicYear;
    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt;

    public Deliberation() {
    }

    public Deliberation(String academicYear, Student student, Level level, Double finalGrade, Boolean passed, Integer rank) {
        this.finalGrade = finalGrade;
        this.passed = passed;
        this.rank = rank;
        this.academicYear = academicYear;
        this.student = student;
        this.level = level;
    }

    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id")
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "level_id")
    private Level level;
}
