package com.example.GestionNote.repository;

import com.example.GestionNote.model.Level;
import com.example.GestionNote.model.LevelPath;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LevelPathRepository extends JpaRepository<LevelPath, Integer> {

    List<LevelPath> findByLevel(Level level);

    List<LevelPath> getAllByLevel(Level level);
}
