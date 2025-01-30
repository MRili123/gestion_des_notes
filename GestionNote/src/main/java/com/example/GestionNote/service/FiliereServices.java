package com.example.GestionNote.service;

import com.example.GestionNote.model.Filiere;
import com.example.GestionNote.model.Professor;
import com.example.GestionNote.repository.FiliereRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class FiliereServices {
    @Autowired
    private FiliereRepository filiereRepository;
    @Autowired
    private ProfessorServices professorServices;

    public List<Filiere> getAllFilieres(){
        return filiereRepository.findByDeleted(false);
    }

    public Filiere getFiliereById(int id){
        return filiereRepository.findById(id).orElse(null);
    }

    public Boolean deleteFiliere(int id){
        Filiere filiere = filiereRepository.findById(id).orElse(null);
        if (filiere != null) {
            filiere.setDeleted(true);
            filiereRepository.save(filiere);
            return true;
        }
        return false;
    }

    public Boolean createFiliere(Filiere newFiliere) {
        Filiere filiere = new Filiere();
        Professor professor = professorServices.getProfessorById(newFiliere.getCoordinator().getId());
        if (professor != null) {
            filiere.setCoordinator(professor);
            filiere.setTitle(newFiliere.getTitle());
            filiere.setAlias(newFiliere.getAlias());
            filiere.setAccreditationStart(newFiliere.getAccreditationStart());
            filiere.setAccreditationEnd(newFiliere.getAccreditationEnd());
            filiere.setCreatedAt(LocalDateTime.now());
            filiereRepository.save(filiere);
            return true;
        }
        return false;
    }

    public Boolean updateFiliere(int id, Filiere updatedFiliere) {
        Filiere filiere = filiereRepository.findById(id).orElse(null);
        if (filiere != null) {
            Professor professor = professorServices.getProfessorById(updatedFiliere.getCoordinator().getId());
            if (professor != null) {
                filiere.setCoordinator(professor);
                filiere.setTitle(updatedFiliere.getTitle());
                filiere.setAlias(updatedFiliere.getAlias());
                filiere.setAccreditationStart(updatedFiliere.getAccreditationStart());
                filiere.setAccreditationEnd(updatedFiliere.getAccreditationEnd());
                filiere.setUpdatedAt(LocalDateTime.now());
                filiereRepository.save(filiere);
                return true;
            }
        }
        return false;
    }
}
