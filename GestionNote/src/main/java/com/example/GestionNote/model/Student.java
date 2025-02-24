package com.example.GestionNote.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "students")
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String cne;
    private String firstName;
    private String lastName;
    private LocalDateTime createdAt = LocalDateTime.now();
    private boolean deleted = false;

    // constrcuters
    public Student() {
    }

    public Student(String cne, String firstName, String lastName, Level currentLevel) {
        this.cne = cne;
        this.firstName = firstName;
        this.lastName = lastName;
        this.currentLevel = currentLevel;
    }

    // Relationships
    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<StudentDataHistory> studentDataHistories;

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Exam> exams;

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Enrollment> enrollments;

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Deliberation> deliberations;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "current_level_id")
    private Level currentLevel;
}
