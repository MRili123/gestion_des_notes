package com.example.GestionNote.model;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "filieres")
public class Filiere {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String title;
    private String alias;
    private LocalDate accreditationStart; // Date de debut d'accreditation
    private LocalDate accreditationEnd;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean deleted = false;

    // Relationships
    @OneToMany(mappedBy = "filiere", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Level> levels;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coordinator_id")
    private Professor coordinator;

    // Constructors
    public Filiere() {
        // Initialize default values if necessary
    }

    public Filiere(String title, String alias, LocalDate accreditationStart, LocalDate accreditationEnd, Professor coordinator) {
        this.title = title;
        this.alias = alias;
        this.accreditationStart = accreditationStart;
        this.accreditationEnd = accreditationEnd;
        this.createdAt = LocalDateTime.now();
        this.coordinator = coordinator;
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

    public LocalDate getAccreditationStart() {
        return accreditationStart;
    }

    public void setAccreditationStart(LocalDate accreditationStart) {
        this.accreditationStart = accreditationStart;
    }

    public LocalDate getAccreditationEnd() {
        return accreditationEnd;
    }

    public void setAccreditationEnd(LocalDate accreditationEnd) {
        this.accreditationEnd = accreditationEnd;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Set<Level> getLevels() {
        return levels;
    }

    public void setLevels(Set<Level> levels) {
        this.levels = levels;
    }

    public Professor getCoordinator() {
        return coordinator;
    }

    public void setCoordinator(Professor coordinator) {
        this.coordinator = coordinator;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
