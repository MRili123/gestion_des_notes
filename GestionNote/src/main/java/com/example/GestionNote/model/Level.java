package com.example.GestionNote.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "levels")
public class Level {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;// Course
    private String title; // titre du niveau (classe)
    private String alias; // abbreviation du niveau
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean deleted = false;

    // Relationships
    @OneToMany(mappedBy = "level", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Deliberation> deliberations;

    @OneToMany(mappedBy = "level", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<LevelPath> currentLevelPaths;

    @OneToMany(mappedBy = "nextLevel", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<LevelPath> nextLevelPaths;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "filiere_id")
    private Filiere filiere;

    @OneToMany(mappedBy = "level", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Module> modules;

    // Constructors
    public Level() {
        // Initialize default values if necessary
    }

    public Level(String title, String alias, Filiere filiere) {
        this.title = title;
        this.alias = alias;
        this.filiere = filiere;
        this.createdAt = LocalDateTime.now();
    }
}