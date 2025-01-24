package com.example.GestionNote.repository;

import com.example.GestionNote.model.Module;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ModuleRepository extends JpaRepository<Module, Integer> {
    List<Module> findByLevelId(Integer levelId); // Find modules by level
}
