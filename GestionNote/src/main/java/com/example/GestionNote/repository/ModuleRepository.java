package com.example.GestionNote.repository;

import com.example.GestionNote.model.Module;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


public interface ModuleRepository extends JpaRepository<Module, Integer> {
    @Query("SELECT m FROM Module m JOIN m.level JOIN m.professor WHERE m.deleted = false")
    List<Module> findAllByDeleted(Boolean deleted);

    List<Module> findAllByLevelIdAndDeleted(Integer levelId, Boolean deleted);

    Module findByCode(String code);
}
