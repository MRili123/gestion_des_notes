package com.example.GestionNote.repository;

import com.example.GestionNote.model.Level;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface LevelRepository extends JpaRepository<Level, Integer> {
    @Query("SELECT l FROM Level l JOIN FETCH l.filiere WHERE l.deleted = :b")
    List<Level> findByDeleted(boolean b);
}
