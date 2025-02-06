package com.example.GestionNote.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "levels_paths")
public class LevelPath {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "level_id")
    private Level level;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "next_level_id")
    private Level nextLevel;

    // Constructors
    public LevelPath() {
        // Initialize default values if necessary
    }

    public LevelPath(Level level, Level nextLevel) {
        this.level = level;
        this.nextLevel = nextLevel;
        this.createdAt = LocalDateTime.now();
    }


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Level getLevel() {
        return level;
    }

    public void setLevel(Level level) {
        this.level = level;
    }

    public Level getNextLevel() {
        return nextLevel;
    }

    public void setNextLevel(Level nextLevel) {
        this.nextLevel = nextLevel;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
