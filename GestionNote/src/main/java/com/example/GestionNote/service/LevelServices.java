package com.example.GestionNote.service;

import com.example.GestionNote.DTO.LevelDTO;
import com.example.GestionNote.model.Filiere;
import com.example.GestionNote.model.Level;
import com.example.GestionNote.model.LevelPath;
import com.example.GestionNote.repository.LevelPathRepository;
import com.example.GestionNote.repository.LevelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class LevelServices {
    @Autowired
    private LevelRepository levelRepository;
    @Autowired
    private FiliereServices filiereServices;
    @Autowired
    private LevelPathRepository levelPathRepository;

    public List<Level> getAllLevels(){
        return levelRepository.findByDeleted(false);
    }

    public Level getLevelById(int id){
        return levelRepository.findById(id).orElse(null);
    }

    public Boolean deleteLevel(int id){
        Level level = levelRepository.findById(id).orElse(null);
        if (level != null) {
            level.setDeleted(true);
            levelRepository.save(level);
            return true;
        }
        return false;
    }

    public Boolean createLevel(LevelDTO newLevel) {
        Filiere filiere = filiereServices.getFiliereById(newLevel.getFiliereId());
        if (filiere != null) {
            Level level = new Level();
            level.setFiliere(filiere);
            level.setTitle(newLevel.getTitle());
            level.setAlias(newLevel.getAlias());
            level.setCreatedAt(LocalDateTime.now());
            levelRepository.save(level);

            // If the level has no next levels, add a LevelPath with nextLevel set to null
            if (newLevel.getNextLevels().contains(-1) || newLevel.getNextLevels().isEmpty()) {
                LevelPath levelPath = new LevelPath();
                levelPath.setLevel(level);
                levelPath.setNextLevel(null);
                levelPath.setCreatedAt(LocalDateTime.now());
                levelPathRepository.save(levelPath);
            }
            // Otherwise, add all next levels to the level
            else {
                for (int nextLevelId : newLevel.getNextLevels()) {
                    LevelPath levelPath = new LevelPath();
                    levelPath.setLevel(level);
                    levelPath.setNextLevel(levelRepository.findById(nextLevelId).orElse(null));
                    levelPath.setCreatedAt(LocalDateTime.now());
                    levelPathRepository.save(levelPath);
                }
            }
            return true;
        }
        return false;
    }

    public Boolean updateLevel(int id, LevelDTO updatedLevel) {
        Level level = levelRepository.findById(id).orElse(null);
        if (level != null) {
            Filiere filiere = filiereServices.getFiliereById(updatedLevel.getFiliereId());
            if (filiere != null) {
                level.setFiliere(filiere);
                level.setTitle(updatedLevel.getTitle());
                level.setAlias(updatedLevel.getAlias());
                level.setUpdatedAt(LocalDateTime.now());
                levelRepository.save(level);

                // Delete all current level paths
                List<LevelPath> currentLevelPaths = levelPathRepository.findByLevel(level);
                levelPathRepository.deleteAll(currentLevelPaths);

                // If the level has no next levels, add a LevelPath with nextLevel set to null
                if (updatedLevel.getNextLevels().contains(-1) || updatedLevel.getNextLevels().isEmpty()) {
                    LevelPath levelPath = new LevelPath();
                    levelPath.setLevel(level);
                    levelPath.setNextLevel(null);
                    levelPath.setCreatedAt(LocalDateTime.now());
                    levelPathRepository.save(levelPath);
                }
                // Otherwise, add all next levels to the level
                else {
                    for (int nextLevelId : updatedLevel.getNextLevels()) {
                        LevelPath levelPath = new LevelPath();
                        levelPath.setLevel(level);
                        levelPath.setNextLevel(levelRepository.findById(nextLevelId).orElse(null));
                        levelPath.setCreatedAt(LocalDateTime.now());
                        levelPathRepository.save(levelPath);
                    }
                }
                return true;
            }
        }
        return false;
    }
}
