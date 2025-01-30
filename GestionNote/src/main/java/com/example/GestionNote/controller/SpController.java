package com.example.GestionNote.controller;

import com.example.GestionNote.DTO.CustomUserDetails;
import com.example.GestionNote.DTO.LevelDTO;
import com.example.GestionNote.DTO.ProfessorDTO;
import com.example.GestionNote.model.*;
import com.example.GestionNote.repository.*;
import com.example.GestionNote.service.UserServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/AdminSp")
public class SpController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProfessorRepository professorRepository;
    @Autowired
    private FiliereRepository filiereRepository;
    @Autowired
    private LevelRepository levelRepository;
    @Autowired
    private LevelPathRepository levelPathRepository;

    // Make the user object accessible to all controller routes
    @ModelAttribute
    public void addUserToModel(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof UserDetails) {
                UserDetails userDetails = (UserDetails) principal;
                String username = userDetails.getUsername();
                User user = userRepository.findByUsername(username);
                model.addAttribute("user", user);
            }
        }
    }

    @RequestMapping("/home")
    public String home() {
        return "AdminSp/home";
    }

    @RequestMapping("/professors")
    public String professors(Model model) {
        List<Professor> professors = professorRepository.findByDeleted(false);
        model.addAttribute("professors", professors);
        return "AdminSp/professors";
    }

    @RequestMapping("/professors/delete/{id}")
    public String deleteProfessor(@PathVariable int id) {
        Professor professor = professorRepository.findById(id).orElse(null);
        if (professor != null) {
            professor.setDeleted(true);
            professorRepository.save(professor);
        }
        return "redirect:/AdminSp/professors";
    }

    @RequestMapping("/professors/edit/{id}")
    public String editProfessor(@PathVariable int id, @RequestBody ProfessorDTO updatedProfessor) {
        Professor professor = professorRepository.findById(id).orElse(null);
        if (professor != null) {
            professor.setFirstName(updatedProfessor.getFirstName());
            professor.setLastName(updatedProfessor.getLastName());
            professor.setCin(updatedProfessor.getCin());
            professor.setEmail(updatedProfessor.getEmail());
            professor.setPhone(updatedProfessor.getPhone());
            professorRepository.save(professor);
        }
        return "redirect:/AdminSp/professors";
    }

    @RequestMapping("/professors/add")
    public String addProfessor(@RequestBody ProfessorDTO newProfessor) {
        Professor professor = new Professor();
        professor.setFirstName(newProfessor.getFirstName());
        professor.setLastName(newProfessor.getLastName());
        professor.setCin(newProfessor.getCin());
        professor.setEmail(newProfessor.getEmail());
        professor.setPhone(newProfessor.getPhone());
        professor.setCreatedAt(LocalDateTime.now());
        professorRepository.save(professor);
        return "redirect:/AdminSp/professors";
    }

    @RequestMapping("/filieres")
    public String filiere(Model model) {
        List<Filiere> filieres = filiereRepository.findByDeleted(false);
        List<Professor> professors = professorRepository.findByDeleted(false);
        model.addAttribute("filieres", filieres);
        model.addAttribute("professors", professors);
        return "AdminSp/filieres";
    }

    @RequestMapping("/filieres/delete/{id}")
    public String deleteFiliere(@PathVariable int id) {
        Filiere filiere = filiereRepository.findById(id).orElse(null);
        if (filiere != null) {
            filiere.setDeleted(true);
            filiereRepository.save(filiere);
        }
        return "redirect:/AdminSp/filieres";
    }

    @RequestMapping("/filieres/add")
    public String addFiliere(@RequestBody Filiere newFiliere) {
        Filiere filiere = new Filiere();
        Professor professor = professorRepository.findById(newFiliere.getCoordinator().getId()).orElse(null);
        if (professor != null) {
            filiere.setCoordinator(professor);
            filiere.setTitle(newFiliere.getTitle());
            filiere.setAlias(newFiliere.getAlias());
            filiere.setAccreditationStart(newFiliere.getAccreditationStart());
            filiere.setAccreditationEnd(newFiliere.getAccreditationEnd());
            filiere.setCreatedAt(LocalDateTime.now());
            filiereRepository.save(filiere);
        }
        return "redirect:/AdminSp/filieres";
    }

    @RequestMapping("/filieres/edit/{id}")
    public String editFiliere(@PathVariable int id, @RequestBody Filiere updatedFiliere) {
        Filiere filiere = filiereRepository.findById(id).orElse(null);
        if (filiere != null) {
            Professor professor = professorRepository.findById(updatedFiliere.getCoordinator().getId()).orElse(null);
            if (professor != null) {
                filiere.setCoordinator(professor);
                filiere.setTitle(updatedFiliere.getTitle());
                filiere.setAlias(updatedFiliere.getAlias());
                filiere.setAccreditationStart(updatedFiliere.getAccreditationStart());
                filiere.setAccreditationEnd(updatedFiliere.getAccreditationEnd());
                filiere.setUpdatedAt(LocalDateTime.now());
                filiereRepository.save(filiere);
            }
        }
        return  "redirect:/AdminSp/filieres";
    }

    @RequestMapping("/levels")
    public String classes(Model model) {
        List<Filiere> filieres = filiereRepository.findByDeleted(false);
        List<Level> levels = levelRepository.findByDeleted(false);
        model.addAttribute("filieres", filieres);
        model.addAttribute("levels", levels);
        return "AdminSp/levels";
    }

    @RequestMapping("/levels/delete/{id}")
    public String deleteLevel(@PathVariable int id) {
        Level level = levelRepository.findById(id).orElse(null);
        if (level != null) {
            level.setDeleted(true);
            levelRepository.save(level);
        }
        return "redirect:/AdminSp/levels";
    }

    @RequestMapping("/levels/add")
    public String addLevel(@RequestBody LevelDTO newLevel) {
        Filiere filiere = filiereRepository.findById(newLevel.getFiliereId()).orElse(null);
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
        }
        return "redirect:/AdminSp/levels";
    }

    @RequestMapping("/levels/edit/{id}")
    public String editLevel(@PathVariable int id, @RequestBody LevelDTO updatedLevel) {
        Level level = levelRepository.findById(id).orElse(null);
        Filiere filiere = filiereRepository.findById(updatedLevel.getFiliereId()).orElse(null);
        if (level != null) {
            level.setTitle(updatedLevel.getTitle());
            level.setAlias(updatedLevel.getAlias());
            level.setUpdatedAt(LocalDateTime.now());
            level.setFiliere(filiere);
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
        }
        return "redirect:/AdminSp/levels";
    }

    @RequestMapping("/modules")
    public String modules() {
        return "AdminSp/modules";
    }

    @RequestMapping("/elements")
    public String elements() {
        return "AdminSp/elements";
    }



}
