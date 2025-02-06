package com.example.GestionNote.repository;

import com.example.GestionNote.model.Filiere;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FiliereRepository extends JpaRepository<Filiere, Integer> {
    List<Filiere> findByDeleted(Boolean deleted);
    Filiere findByAlias(String alias);
}
