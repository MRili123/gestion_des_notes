package com.example.GestionNote.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "levels")
public class Level {
    @Id
    private Integer id;// Course
    private String title; // titre du niveau (classe)
    private String alias; // abbreviation du niveau
    private LocalDateTime createdAt;

    // Relationships
    @OneToMany(mappedBy = "level", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Deliberation> deliberations;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "filiere_id")
    private Filiere filiere;

    @OneToMany(mappedBy = "level", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Module> modules;


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
}
