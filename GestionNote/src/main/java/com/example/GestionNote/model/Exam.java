package com.example.GestionNote.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "exams")
public class Exam {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Double grade;
    private String session;
    private String academicYear;
    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt;

    // Constructors
    public Exam() {
    }

    public Exam(Double grade, String session, String academicYear, Student student, Element element) {
        this.grade = grade;
        this.session = session;
        this.academicYear = academicYear;
        this.student = student;
        this.element = element;
    }

    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id")
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "element_id")
    private Element element;
}
