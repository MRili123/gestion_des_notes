package com.example.GestionNote.repository;

import com.example.GestionNote.model.Element;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ElementRepository extends JpaRepository<Element, Integer> {
    List<Element> findByModuleId(Integer moduleId); // Find elements by module
}
