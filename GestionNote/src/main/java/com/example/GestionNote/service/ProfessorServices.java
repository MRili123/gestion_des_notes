package com.example.GestionNote.service;

import com.example.GestionNote.DTO.ProfessorDTO;
import com.example.GestionNote.model.Professor;
import com.example.GestionNote.repository.ProfessorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ProfessorServices {
    @Autowired
    private ProfessorRepository professorRepository;

    public List<Professor> getAllProfessors() {
        return professorRepository.findByDeleted(false);
    }

    public Professor getProfessorById(int id) {
        return professorRepository.findById(id).orElse(null);
    }

    public Boolean deleteProfessor(int id) {
        Professor professor = professorRepository.findById(id).orElse(null);
        if (professor != null) {
            professor.setDeleted(true);
            professorRepository.save(professor);
            return true;
        }
        return false;
    }

    public Boolean updateProfessor(int id, ProfessorDTO updatedProfessor) {
        Professor professor = professorRepository.findById(id).orElse(null);
        if (professor != null) {
            professor.setFirstName(updatedProfessor.getFirstName());
            professor.setLastName(updatedProfessor.getLastName());
            professor.setCin(updatedProfessor.getCin());
            professor.setEmail(updatedProfessor.getEmail());
            professor.setPhone(updatedProfessor.getPhone());
            professorRepository.save(professor);
            return true;
        }
        return false;
    }

    public Boolean createProfessor(ProfessorDTO newProfessor) {
        Professor professor = new Professor();
        professor.setFirstName(newProfessor.getFirstName());
        professor.setLastName(newProfessor.getLastName());
        professor.setCin(newProfessor.getCin());
        professor.setEmail(newProfessor.getEmail());
        professor.setPhone(newProfessor.getPhone());
        professor.setCreatedAt(LocalDateTime.now());
        professorRepository.save(professor);
        return true;
    }

}
