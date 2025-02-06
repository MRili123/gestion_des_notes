package com.example.GestionNote.repository;

import com.example.GestionNote.model.Element;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ElementRepository extends JpaRepository<Element, Integer> {
    List<Element> findAllByDeleted(Boolean deleted);
}
