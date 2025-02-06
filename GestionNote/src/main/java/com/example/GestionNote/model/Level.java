package com.example.GestionNote.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Set;

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


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Set<Deliberation> getDeliberations() {
        return deliberations;
    }

    public void setDeliberations(Set<Deliberation> deliberations) {
        this.deliberations = deliberations;
    }

    public Filiere getFiliere() {
        return filiere;
    }

    public void setFiliere(Filiere filiere) {
        this.filiere = filiere;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public Set<LevelPath> getCurrentLevelPaths() {
        return currentLevelPaths;
    }

    public void setCurrentLevelPaths(Set<LevelPath> currentLevelPaths) {
        this.currentLevelPaths = currentLevelPaths;
    }

    public Set<LevelPath> getNextLevelPaths() {
        return nextLevelPaths;
    }

    public void setNextLevelPaths(Set<LevelPath> nextLevelPaths) {
        this.nextLevelPaths = nextLevelPaths;
    }

    public Set<Module> getModules() {
        return modules;
    }

    public void setModules(Set<Module> modules) {
        this.modules = modules;
    }
}
